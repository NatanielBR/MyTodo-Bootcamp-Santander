package com.natanielbr.mytodo.models

import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import com.natanielbr.mytodo.models.dataSource.model.TodoItem
import com.natanielbr.mytodo.utils.MutableListLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * ViewModel que irá armazenar um unico item que será utilizado para editar (ou null, para criar)
 * alem da lista de itens que irá ser exibida para o usuario.
 */
class TodoItemViewModel : ViewModel() {
    // Na hora de abrir o editor
    // se ele for null, será para criar
    // se houver algo, será pra editar
    var selectedItem: TodoItem? = null

    val items: MutableListLiveData<TodoItem> by lazy {
        MutableListLiveData()
    }

    /**
     * Metodo que irá realizar a consulta ao Datasource.
     * Este metodo é uma thread de trabalho que não deve
     * ser executada na thread main.
     */
    @WorkerThread
    suspend fun getAll() {
        withContext(Dispatchers.IO) {
            val dataSource = TodoItemRepository.dataSource

            val list = dataSource.getAll()

            items.transaction {
                clear()
                addAll(list)
            }

        }
    }
}