package com.natanielbr.mytodo.models

import com.natanielbr.mytodo.models.dataSource.DataSource

object TodoItemRepository {
    lateinit var dataSource: DataSource
        private set

    fun init(dataSource: DataSource) {
        this.dataSource = dataSource
    }

    fun isInitialized(): Boolean{
        return TodoItemRepository::dataSource.isInitialized
    }
}