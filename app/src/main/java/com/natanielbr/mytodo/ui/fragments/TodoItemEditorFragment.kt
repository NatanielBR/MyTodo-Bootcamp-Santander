package com.natanielbr.mytodo.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.natanielbr.mytodo.R
import com.natanielbr.mytodo.databinding.TodoItemEditorFragmentBinding
import com.natanielbr.mytodo.models.TodoItemRepository
import com.natanielbr.mytodo.models.TodoItemViewModel
import com.natanielbr.mytodo.models.dataSource.model.TodoItem
import java.text.DateFormat
import java.util.*

class TodoItemEditorFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private val todoModel: TodoItemViewModel by activityViewModels()

    lateinit var target: TodoItem

    lateinit var calendar: Calendar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            return super.onCreateView(inflater, container, savedInstanceState)
        }

        val binder = TodoItemEditorFragmentBinding.inflate(inflater, container, false)

        if (todoModel.selectedItem != null) {
            /**
             * Irá copiar o selectedItem para o target (isso é feito para evitar que o objeto da UI
             * seja atualizado mesmo não salvando - será atualizado no visual não no Datasource)
             * e transformar o tempo (em Epoch) para Calendar.
             */
            val it = todoModel.selectedItem!!
            target = it.copy()
            calendar = Calendar.getInstance()
            val date = Date(it.target)

            calendar.time = date

            binder.editTextTextPersonName.setText(it.name)
            binder.dateField.setText(formatDate(date, requireContext()))
            binder.timeField.setText(formatTime(date, requireContext()))
        } else {
            /**
             * Irá criar um novo item e utilizar o tempo atual para preencher o tempo desse item.
             */
            target = TodoItem(name = "", target = 0)

            calendar = Calendar.getInstance()
            val date = calendar.time

            binder.dateField.setText(formatDate(date, requireContext()))
            binder.timeField.setText(formatTime(date, requireContext()))
        }

        /**
         * Tanto no dateField como no timeField é utilizado uma tecnica para
         * impedir que o usuario altere o valor no teclado.
         * Sempre que o field receber o foco, ele irá abrir o picker
         * e limpar o foco, para que o ciclo se repita.
         */

        binder.dateField.setOnFocusChangeListener { v, hasFocus ->
            // para a primeira vez que clicar no campo
            // o primeiro click não é considerado um click de fato
            // mas sim uma mudança de focus, então ele cai aqui.
            // Apartir do segundo ele não cai aqui mas cai no
            // clickListener
            if (!hasFocus) return@setOnFocusChangeListener
            showCalendarPicker(v.context)
            v.clearFocus()
        }

        binder.timeField.setOnFocusChangeListener { v, hasFocus ->
            // a logica é a mesma que do dateField
            if (!hasFocus) return@setOnFocusChangeListener
            showTimePicker(v.context)
            v.clearFocus()
        }

        binder.saveFloatButton.setOnClickListener {
            val target = target

            target.name = getNameTextField().text.toString()
            target.target = calendar.timeInMillis

            // Não fiz isso em dentro de uma coroutine por que
            // o update no UI não irá funcionar de forma bem
            // Percebi que o isert é feito de forma rapida
            // então não vai impactar ao usuario
            target.enabled = true
            val item = TodoItemRepository.dataSource
                .insert(target)

            item.scheduleNotification(requireContext())

            todoModel.selectedItem = null

            findNavController().navigate(R.id.toHome1)
        }



        return binder.root
    }

    private fun getNameTextField(): TextInputEditText {
        return requireView().findViewById(R.id.editTextTextPersonName)
    }

    private fun getDateTextField(): TextInputEditText {
        return requireView().findViewById(R.id.dateField)
    }

    private fun getTimeTextField(): TextInputEditText {
        return requireView().findViewById(R.id.timeField)
    }

    /**
     * Exibe o DatePicker, se baseiando na data do Target.
     */
    private fun showCalendarPicker(context: Context) {
        val picker = DatePickerDialog(
            context,
            this@TodoItemEditorFragment,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        picker.show()
    }

    /**
     * Exibe o TimePicker, se baseiando na hora do Target.
     */
    private fun showTimePicker(context: Context) {
        val picker = TimePickerDialog(
            context, this@TodoItemEditorFragment,
            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
        )

        picker.show()
    }

    /**
     * Irá formatar a data para a formato mais humano.
     */
    private fun formatDate(date: Date, context: Context): String {
        val dateFormat: DateFormat = android.text.format.DateFormat.getDateFormat(context)

        return (dateFormat.format(date))
    }

    /**
     * Irá formatar o horario para o formato mais humano.
     */
    private fun formatTime(date: Date, context: Context): String {
        val dateFormat: DateFormat = android.text.format.DateFormat.getTimeFormat(context)

        return (dateFormat.format(date))
    }

    /**
     * Listener que irá passar a data que o usuario escolheu para o Calendar
     * e atualizar o campo.
     */
    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        getDateTextField().setText(formatDate(calendar.time, view.context))
    }

    /**
     * Listener que irá passar o horario que o usuario escolheu para o Calendar
     * e atualiar o campo.
     */
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        getTimeTextField().setText(formatTime(calendar.time, view.context))
    }
}