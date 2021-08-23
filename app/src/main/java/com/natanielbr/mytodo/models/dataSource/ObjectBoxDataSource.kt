package com.natanielbr.mytodo.models.dataSource

import android.content.Context
import com.natanielbr.mytodo.models.dataSource.box.ObjectBox
import com.natanielbr.mytodo.models.dataSource.box.entitys.TodoItemEntity
import com.natanielbr.mytodo.models.dataSource.box.entitys.TodoItemEntity.Companion.toEntity
import com.natanielbr.mytodo.models.dataSource.box.entitys.TodoItemEntity.Companion.toModel
import com.natanielbr.mytodo.models.dataSource.box.entitys.TodoItemEntity.Companion.toModelNull
import com.natanielbr.mytodo.models.dataSource.model.TodoItem
import io.objectbox.Box

/**
 * Datasource persistente que será utilizada durante a produção
 * e em momentos em que seja necessario. Ele irá utilizar o
 * ObjectBox como forma de armazenar, não utilizei o Room e
 * irei explicar no README.
 */
class ObjectBoxDataSource(context: Context) : DataSource() {
    private val box: Box<TodoItemEntity>

    init {
        ObjectBox.init(context)
        box = ObjectBox.store.boxFor(TodoItemEntity::class.java)
    }

    override fun getAll(): List<TodoItem> {
        return box.all.toModel()
    }

    override fun get(id: Int): TodoItem? {
        return box[id.toLong()].toModelNull()
    }

    override fun exists(id: Int): Boolean {
        return box.contains(id.toLong())
    }

    override fun insert(item: TodoItem): TodoItem {
        val id = box.put(item.toEntity())

        return item.copy(id = id.toInt())
    }

    override fun remove(item: TodoItem): Boolean {
        return remove(item.id)
    }

    override fun remove(id: Int): Boolean {
        return box.remove(id.toLong())
    }

}