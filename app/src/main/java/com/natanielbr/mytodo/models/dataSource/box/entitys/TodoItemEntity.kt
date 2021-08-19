package com.natanielbr.mytodo.models.dataSource.box.entitys

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@Entity
data class TodoItemEntity(
    @Id var id: Long = 0,
    @Unique var name: String = "",
    val created: Long = 0, // In Epoch
    var updated: Long = 0, // In Epoch
)