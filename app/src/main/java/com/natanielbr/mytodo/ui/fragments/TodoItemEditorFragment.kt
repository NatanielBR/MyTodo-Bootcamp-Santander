package com.natanielbr.mytodo.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.natanielbr.mytodo.R
import com.natanielbr.mytodo.databinding.TodoItemEditorFragmentBinding
import com.natanielbr.mytodo.models.TodoItemRepository
import com.natanielbr.mytodo.models.TodoItemViewModel
import com.natanielbr.mytodo.models.dataSource.model.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

class TodoItemEditorFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    val todoModel: TodoItemViewModel by activityViewModels()

    var target: TodoItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            return super.onCreateView(inflater, container, savedInstanceState)
        }

        val binder = TodoItemEditorFragmentBinding.inflate(inflater, container, false)

        todoModel.selectedItem?.also {
            target = it.copy()

            Log.d("neodev", "Carregando: $target")

            binder.editTextTextPersonName.setText(it.name)

            val date = Date(it.target)

            binder.dateField.setText(formatDate(date, requireContext()))
            binder.timeField.setText(formatTime(date, requireContext()))
        }

        binder.dateField.setOnFocusChangeListener { v, hasFocus ->
            // para a primeira vez que clicar no campo
            // o primeiro click não é considerado um click de fato
            // mas sim uma mudança de focus, então ele cai aqui.
            // Apartir do segundo ele não cai aqui mas cai no
            // clickListener
            if (!hasFocus) return@setOnFocusChangeListener
            showCalendarPicker(v.context)
            getTimeTextField().setText("")
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

            if (target != null) {
                target.name = getNameTextField().text.toString()

                requireActivity().lifecycleScope.launch(Dispatchers.IO) {
                    Log.d("neodev", "Salvando $target")
                    TodoItemRepository.dataSource
                        .insert(target)
                }

                todoModel.selectedItem = null

                findNavController().navigate(R.id.toHome1)
            }
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

    private fun showCalendarPicker(context: Context) {
        val now = Calendar.getInstance()

        val picker = DatePickerDialog(
            context, this@TodoItemEditorFragment,
            now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        )

        picker.show()
    }

    private fun showTimePicker(context: Context) {
        val now = Calendar.getInstance()

        val picker = TimePickerDialog(
            context, this@TodoItemEditorFragment,
            now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true
        )

        picker.show()
    }

    private fun formatDate(date: Date, context: Context): String {
        val dateFormat: DateFormat = android.text.format.DateFormat.getDateFormat(context)

        return (dateFormat.format(date))
    }

    private fun formatTime(date: Date, context: Context): String {
        val dateFormat: DateFormat = android.text.format.DateFormat.getTimeFormat(context)

        return (dateFormat.format(date))
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        target?.target = calendar.timeInMillis

        getDateTextField().setText(formatDate(calendar.time, view.context))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        target?.target = calendar.timeInMillis

        getTimeTextField().setText(formatTime(calendar.time, view.context))
    }
}