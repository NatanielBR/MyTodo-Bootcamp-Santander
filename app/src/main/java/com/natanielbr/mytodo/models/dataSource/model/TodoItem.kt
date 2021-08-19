package com.natanielbr.mytodo.models.dataSource.model

data class TodoItem(
    val id: Long = -1,
    var name: String,
    var target: Long, // In Epoch
) {
}