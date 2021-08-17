package com.natanielbr.mytodo.models.data

data class TodoItem(
    var name: String,
    val created: Long, // In Epoch
    var updated: Long, // In Epoch
)