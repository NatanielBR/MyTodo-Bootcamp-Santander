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
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.material.textfield.TextInputEditText
import com.natanielbr.mytodo.R
import com.natanielbr.mytodo.databinding.TodoItemEditorFragmentBinding
import com.natanielbr.mytodo.models.TodoItemRepository
import com.natanielbr.mytodo.models.TodoItemViewModel
import com.natanielbr.mytodo.models.dataSource.model.TodoItem
import com.natanielbr.mytodo.ui.services.TodoNotifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

class TodoItemEditorFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private val todoModel: TodoItemViewModel by activityViewModels()

    var target: TodoItem? = null

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

        todoModel.selectedItem?.also {
            target = it.copy()
            calendar = Calendar.getInstance()
            val date = Date(it.target)

            calendar.time = date

            binder.editTextTextPersonName.setText(it.name)
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
                target.target = calendar.timeInMillis

                // Não fiz isso em dentro de uma coroutine por que
                // o update no UI não irá funcionar de forma bem
                // Percebi que o Save é feito de forma rapida
                // então não vai impactar ao usuario
                target.enabled = true
                val item = TodoItemRepository.dataSource
                    .insert(target)

                item.scheduleNotification(requireContext())

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
        val picker = DatePickerDialog(
            context, this@TodoItemEditorFragment,
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )

        picker.show()
    }

    private fun showTimePicker(context: Context) {
        val picker = TimePickerDialog(
            context, this@TodoItemEditorFragment,
            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
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

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        getDateTextField().setText(formatDate(calendar.time, view.context))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        getTimeTextField().setText(formatTime(calendar.time, view.context))
    }
}