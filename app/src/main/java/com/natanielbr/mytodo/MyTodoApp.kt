package com.natanielbr.mytodo

import android.app.Application
import com.natanielbr.mytodo.models.TodoItemRepository
import com.natanielbr.mytodo.models.dataSource.InMemoryDataSource
import com.natanielbr.mytodo.models.dataSource.box.ObjectBox

class MyTodoApp : Application() {
    override fun onCreate() {
        ObjectBox.init(this)
        TodoItemRepository.init(InMemoryDataSource())
        super.onCreate()
    }
}