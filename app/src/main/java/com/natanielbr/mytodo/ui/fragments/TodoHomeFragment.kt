package com.natanielbr.mytodo.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.natanielbr.mytodo.R
import com.natanielbr.mytodo.databinding.TodoHomeFragmentBinding
import com.natanielbr.mytodo.models.TodoItemRepository
import com.natanielbr.mytodo.models.TodoItemViewModel
import com.natanielbr.mytodo.models.dataSource.model.TodoItem
import com.natanielbr.mytodo.utils.RecyclerViewUtils.setOnItemClickListener
import com.natanielbr.mytodo.utils.TypeUtils.humanizeTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

            bind.todoItens.setOnItemClickListener { recyclerView, position, isLongTouch ->
                val nav = findNavController()
                val adapter = recyclerView.adapter as TodoItemAdapter
                todoModel.selectedItem = adapter.data[position]
                Log.d("neodev", "onCreateView: ${todoModel.selectedItem?.name}")
                nav.navigate(R.id.toEditor)
            }
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
            val recyclerView = getRecyclerView()
            recyclerView.adapter?.notifyDataSetChanged()

            it.findViewById<SwipeRefreshLayout>(R.id.refresh_layout).isRefreshing = false
        }

    }

    /**
     * Ao invez de criar uma variavel para guardar a instancia
     * ou algo assim, vou usar a raiz do fragment e sempre obter
     * através dela. Caso ocorra uma rotação de tela, é possivel
     * que a instancia mude e em um passado isso me deu uma dor
     * de cabeça.
     */
    private fun getRecyclerView(): RecyclerView {
        return requireView().findViewById(R.id.todo_itens)
    }

    override fun onResume() {
        lifecycleScope.launch {
            val data = withContext(Dispatchers.IO) { TodoItemRepository.dataSource.getAll() }

            getRecyclerView().adapter.also {
                it ?: return@also
                it as TodoItemAdapter

                it.data.clear()
                it.data.addAll(data)
                onRefresh()
            }
        }
        super.onResume()
    }

    //region RecyclerView Adapter

    class TodoItemAdapter : RecyclerView.Adapter<TodoItemAdapter.TodoItemViewHolder>() {
        val data = mutableListOf<TodoItem>()

        class TodoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var nameView: TextView = itemView.findViewById(R.id.todo_name_view)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
            val layout = View.inflate(parent.context, R.layout.todo_item_view_holder, null)

            return TodoItemViewHolder(layout)
        }

        override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
            val item = data[position]

            holder.nameView.text = item.name
        }

        override fun getItemCount(): Int {
            return data.size
        }
    }

    //endregion
}