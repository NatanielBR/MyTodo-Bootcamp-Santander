package com.natanielbr.mytodo.models

import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import com.natanielbr.mytodo.models.box.ObjectBox
import com.natanielbr.mytodo.models.box.entitys.TodoItemEntity
import com.natanielbr.mytodo.models.box.entitys.TodoItemEntity_
import com.natanielbr.mytodo.utils.MutableListLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoItemViewModel : ViewModel() {

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