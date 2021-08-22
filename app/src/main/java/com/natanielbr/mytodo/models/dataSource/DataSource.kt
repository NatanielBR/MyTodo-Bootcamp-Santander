package com.natanielbr.mytodo.models.dataSource

import com.natanielbr.mytodo.models.dataSource.model.TodoItem

abstract class DataSource {

    abstract fun getAll(): List<TodoItem>
    abstract fun get(id: Int): TodoItem?

    abstract fun exists(id: Int): Boolean

    abstract fun insert(item: TodoItem): TodoItem

    abstract fun remove(item: TodoItem): Boolean
    abstract fun remove(id: Int): Boolean

}