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
    /**
     * O id do TodoItem, durante o desenvolvimento o valor default era -1 entretando
     * o Objectbox não aceita numero negativo então coloquei como zero.
     *
     */
    @Id var id: Long = 0,
    /**
     * Nome unico, o datasource irá cuidar disso
     */
    @Unique var name: String = "",
    /**
     * A Data em que o Todo_ irá ficar concluido, porem em Epoch
     */
    var target: Long,
    /**
     * Para saber se o Todo_ esta concluido ou não. Isso evita ficar fazendo
     * comparações com o target
     */
    var enabled: Boolean = true
) {

    /**
     * Metodo util para transformar no padrão do DataSource
     */
    fun toModel(): TodoItem {
        return TodoItem(id.toInt(), name, target, enabled)
    }

    companion object {

        /**
         * Metodo util para transformar no padrão do DataSource,
         * nesse caso ele pode ser nulo ou não.
         */
        fun TodoItemEntity?.toModelNull(): TodoItem? {
            if (this == null) {
                return null
            }

            return this.toModel()
        }

        /**
         * Metodo util para transformar no padrão do ObjectBox
         */
        fun TodoItem.toEntity(): TodoItemEntity {
            return TodoItemEntity(id.toLong(), name, target, enabled)
        }

        /**
         * Metodo util para transformar uma lista no padrão do Datasource.
         */
        fun List<TodoItemEntity>.toModel(): List<TodoItem> {
            return map { it.toModel() }
        }
    }
}