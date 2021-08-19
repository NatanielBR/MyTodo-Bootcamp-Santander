package com.natanielbr.mytodo.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
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

class TodoItemEditorFragment : Fragment() {
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

        target = todoModel.selectedItem
        val binder = TodoItemEditorFragmentBinding.inflate(inflater, container, false)

        target?.apply {
            binder.editTextTextPersonName.setText(name)
        }


        binder.saveFloatButton.setOnClickListener {
            val target = target

            if (target != null) {
                target.name = getNameTextField().text.toString()
                target.updated = System.currentTimeMillis()

                requireActivity().lifecycleScope.launch(Dispatchers.IO) {
                    TodoItemRepository.dataSource
                        .insert(target)
                }

                findNavController().navigate(R.id.toHome1)
            }
        }


        return binder.root
    }

    private fun getNameTextField(): TextInputEditText {
        return requireView().findViewById(R.id.editTextTextPersonName)
    }
}