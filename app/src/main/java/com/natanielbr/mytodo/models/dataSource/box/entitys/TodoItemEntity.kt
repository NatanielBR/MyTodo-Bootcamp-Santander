package com.natanielbr.mytodo.models.dataSource.box.entitys

import com.natanielbr.mytodo.models.dataSource.model.TodoItem
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

/**
 * Uma cópia de TodoItem, como data class é final então não posso
 * fazer herança.
 *
 * @see com.natanielbr.mytodo.models.dataSource.model.TodoItem
 */
@Entity
data class TodoItemEntity(
    @Id var id: Long = 0,
    @Unique var name: String = "", // Nome unico, o datasource irá cuidar disso
    var target: Long, // Em Epoch
    var enabled: Boolean = true // Para evitar que notifique algo que já passou
) {

    fun toModel(): TodoItem {
        return TodoItem(id.toInt(), name, target, enabled)
    }

    companion object {

        fun TodoItemEntity?.toModelNull(): TodoItem? {
            if (this == null) {
                return null
            }

            return this.toModel()
        }

        fun TodoItem.toEntity(): TodoItemEntity {
            return TodoItemEntity(id.toLong(), name, target, enabled)
        }

        fun List<TodoItemEntity>.toModel(): List<TodoItem> {
            return map { it.toModel() }
        }
    }
}