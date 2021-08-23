package com.natanielbr.mytodo.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.WorkManager
import com.natanielbr.mytodo.R
import com.natanielbr.mytodo.databinding.TodoHomeFragmentBinding
import com.natanielbr.mytodo.models.TodoItemRepository
import com.natanielbr.mytodo.models.TodoItemViewModel
import com.natanielbr.mytodo.models.dataSource.model.TodoItem
import com.natanielbr.mytodo.utils.TypeUtils.humanizeTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Fragmento inicial, onde irá ser exibido a lista de itens e o botão para adicionar
 * um novo item. Caso clique em um dos itens, será aberto o editor e caso clique no
 * botão irá ser aberto o criador (que no fundo é o mesmo editor ).
 */
class TodoHomeFragment : Fragment() {
    private val todoModel: TodoItemViewModel by activityViewModels()

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
            it.adapter = TodoItemAdapter(this) { item, _ ->
                val nav = findNavController()

                todoModel.selectedItem = item
                nav.navigate(R.id.toEditor)
            }
        }

        bind.newTodoButton.setOnClickListener {
            val nav = findNavController()

            todoModel.selectedItem = null
            nav.navigate(R.id.toEditor)
        }

        todoModel.items.observe(viewLifecycleOwner, {
            it!!

            it.filter { it.enabled }
                .forEach {
                    it.scheduleNotification(requireContext())
                }

            val adapter = (getRecyclerView().adapter as TodoItemAdapter)
            adapter.data.clear()
            adapter.data.addAll(it)
        })

        // Fiz isso para evitar a supressão do aviso Lint.
        // Para evitar suprimir o aviso de forma global
        // separei em um metodo e coloquei o supress lá.
        bind.refreshLayout.setOnRefreshListener(::onRefresh)

        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            todoModel.getAll()
            getRecyclerView().post { onRefresh() }
        }
    }

    /**
     * Utilizo o SwipeRefreshLayout para cria a funcionalidade de refresh.
     * Esse metodo irá obter a View e irá realizar o refresh, lembrando que
     * esse metodo irá somente atualizar a UI, ou seja queira atualizar os
     * itens primeiro atualize no ViewModel e depois chame esse metodo.
     */
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

    /**
     * Cancela o worker relacionado ao todoItem. Esse metodo é
     * seguro caso o Worker não exista ou já esteja cancelado.
     */
    private fun cancelTodo(todoItem: TodoItem) {
        WorkManager.getInstance(requireContext())
            .cancelAllWorkByTag(todoItem.getUniqueName())
    }

    /**
     * Ação quando clica no checkbox, caso marcado ele irá
     * iniciar o Worker, caso desmarcado ele irá cancelar o Worker.
     */
    fun onCheckedChange(button: CompoundButton, ischecked: Boolean) {
        val todoItem: TodoItem = (button.parent as View).findViewById<TextView>(R.id.todo_name_view)
            .text.toString().let { title ->
                todoModel.items.value!!.find { it.name == title }!!
            }
        if (ischecked) {
            todoItem.scheduleNotification(requireContext())
        } else {
            Toast.makeText(
                requireContext(),
                button.context.getText(R.string.canceled),
                Toast.LENGTH_SHORT
            ).show()
            cancelTodo(todoItem)
        }

        todoItem.enabled = ischecked

        requireActivity().lifecycleScope.launch(Dispatchers.IO) {
            TodoItemRepository.dataSource.insert(todoItem)
            withContext(Dispatchers.Main) { onRefresh() }
        }
    }

    //region RecyclerView Adapter

    class TodoItemAdapter(
        private val todoHomeFragment: TodoHomeFragment,
        private val clickListener: (item: TodoItem, position: Int) -> Unit,
    ) :
        RecyclerView.Adapter<TodoItemAdapter.TodoItemViewHolder>() {
        val data = mutableListOf<TodoItem>()

        class TodoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var nameView: TextView = itemView.findViewById(R.id.todo_name_view)
            var enabledView: CheckBox = itemView.findViewById(R.id.todo_enabled_view)
            var delayView: TextView = itemView.findViewById(R.id.todo_delay_view)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
            val layout = LayoutInflater.from(parent.context)
                .inflate(R.layout.todo_item_view_holder, parent, false)

            return TodoItemViewHolder(layout)
        }

        override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
            val item = data[position]

            holder.nameView.text = item.name
            holder.delayView.text = humanizeDelay(item, holder.delayView.context)
            holder.nameView.setOnClickListener {
                clickListener.invoke(item, position)
            }
            holder.enabledView.isChecked = item.enabled
            holder.enabledView.setOnCheckedChangeListener(null)

            holder.enabledView.setOnCheckedChangeListener(todoHomeFragment::onCheckedChange)
        }

        /**
         * Irá transformar o tempo (em Epoch) em um tempo mais humanizado. Ao mesmo tempo
         * irá colocar termos para ajudar a compreensão como:
         * Daqui a 30 segundo(s)
         * Daqui a 1 minuto(s)
         * Daqui a 2 hora(s)
         * Daqui a 3 dias(s)
         */
        private fun humanizeDelay(todoItem: TodoItem, context: Context): String {
            return if (todoItem.target < System.currentTimeMillis() || !todoItem.enabled) {
                context.getString(R.string.finished)
            } else {
                context.getString(R.string.interval_text, todoItem.target.humanizeTime(context))
            }
        }

        override fun getItemCount(): Int {
            return data.size
        }
    }

    //endregion
}