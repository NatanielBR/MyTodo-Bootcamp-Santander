package com.natanielbr.mytodo.models

import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import com.natanielbr.mytodo.models.dataSource.box.ObjectBox
import com.natanielbr.mytodo.models.dataSource.box.entitys.TodoItemEntity
import com.natanielbr.mytodo.models.dataSource.box.entitys.TodoItemEntity_
import com.natanielbr.mytodo.models.dataSource.model.TodoItem
import com.natanielbr.mytodo.utils.MutableListLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoItemViewModel : ViewModel() {
    // Na hora de abrir o editor
    // se ele for null, será para criar
    // se houver algo, será pra editar
    var selectedItem: TodoItem? = null

    val items: MutableListLiveData<TodoItemEntity> by lazy {
        MutableListLiveData()
    }

    @WorkerThread
    suspend fun getAll() {
        withContext(Dispatchers.IO) {
            val box = ObjectBox.store.boxFor(TodoItemEntity::class.java)

            val list = box.query().order(TodoItemEntity_.updated)
                .build().find()

            items.transaction {
                clear()
                addAll(list)
            }

        }
    }
}