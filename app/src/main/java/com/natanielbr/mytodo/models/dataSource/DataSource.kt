package com.natanielbr.mytodo.models.dataSource

import android.content.Context
import com.natanielbr.mytodo.models.dataSource.model.TodoItem

abstract class DataSource(context: Context?) {

    abstract fun getAll(): List<TodoItem>
    abstract fun get(id: Long): TodoItem?

    abstract fun getOrCreate(item: TodoItem): TodoItem

    abstract fun exists(id: Long): Boolean

    abstract fun insert(item: TodoItem): TodoItem

    abstract fun insertAll(items: List<TodoItem>): List<TodoItem>

    abstract fun remove(item: TodoItem): Boolean
    abstract fun remove(id: Long): Boolean

    abstract fun removeAll(ids: List<Long>): Boolean
}