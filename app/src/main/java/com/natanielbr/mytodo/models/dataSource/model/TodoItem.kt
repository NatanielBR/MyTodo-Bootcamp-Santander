package com.natanielbr.mytodo.models.dataSource.model

data class TodoItem(
    val id: Long = 0,
    var name: String = "",
    val created: Long = 0, // In Epoch
    var updated: Long = 0, // In Epoch
) {
}