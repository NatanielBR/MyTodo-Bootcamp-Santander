package com.natanielbr.mytodo.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.natanielbr.mytodo.R
import com.natanielbr.mytodo.databinding.TodoHomeFragmentBinding
import com.natanielbr.mytodo.models.TodoItemViewModel
import com.natanielbr.mytodo.models.box.entitys.TodoItemEntity
import com.natanielbr.mytodo.utils.TypeUtils.humanizeTime

class TodoHomeFragment : Fragment() {
    val todoModel: TodoItemViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            return super.onCreateView(inflater, container, savedInstanceState)
        }

        val bind = TodoHomeFragmentBinding.inflate(inflater, container, false)

        bind.todoItens.also {
            it.layoutManager = LinearLayoutManager(it.context, LinearLayoutManager.VERTICAL, false)
            it.adapter = TodoItemAdapter()
        }


        // Fiz isso para evitar a supressão do aviso Lint.
        // Para evitar suprimir o aviso de forma global
        // separei em um metodo e coloquei o supress lá.
        bind.refreshLayout.setOnRefreshListener(::onRefresh)

        return bind.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onRefresh() {
        requireView().also {
            val recyclerView = it.findViewById<RecyclerView>(R.id.todo_itens)
            recyclerView.adapter?.notifyDataSetChanged()

            it.findViewById<SwipeRefreshLayout>(R.id.refresh_layout).isRefreshing = false
        }

    }


    class TodoItemAdapter : RecyclerView.Adapter<TodoItemAdapter.TodoItemViewHolder>() {
        val data = mutableListOf<TodoItemEntity>()

        class TodoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var nameView: TextView = itemView.findViewById(R.id.todo_name_view)
            val createdView: TextView =
                itemView.findViewById(R.id.todo_created_view) // in Time humanized
            var updatedView: TextView =
                itemView.findViewById(R.id.todo_updated_view) // in Time humanized
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
            val layout = View.inflate(parent.context, R.layout.todo_item_view_holder, null)

            return TodoItemViewHolder(layout)
        }

        override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
            val item = data[position]

            holder.nameView.text = item.name
            holder.createdView.text = item.created.humanizeTime(holder.createdView.context)
            holder.updatedView.text = item.updated.humanizeTime(holder.updatedView.context)
        }

        override fun getItemCount(): Int {
            return data.size
        }
    }
}