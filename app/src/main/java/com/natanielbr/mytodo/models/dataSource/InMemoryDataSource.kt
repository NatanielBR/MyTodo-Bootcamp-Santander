package com.natanielbr.mytodo.models.dataSource

import com.natanielbr.mytodo.models.dataSource.model.TodoItem
import kotlin.math.absoluteValue
import kotlin.random.Random

/**
 * DataSource que irá guardar os dados na memória do App, por um tempo ele foi utilizado
 * para fazer o desenvolvimento do App e deixei aqui caso a ideia vá pra frente.
 */
class InMemoryDataSource : DataSource() {
    private val db: MutableMap<Int, TodoItem> = mutableMapOf()

    override fun getAll(): List<TodoItem> {
        return db.values.toList()
    }

    override fun get(id: Int): TodoItem? {
        return db[id]
    }

    override fun exists(id: Int): Boolean {
        return db.containsKey(id)
    }

    override fun insert(item: TodoItem): TodoItem {
        if (item.id == 0) {
            generateId().also {
                val nItem = item.copy(id = it)
                db[it] = nItem

                return nItem
            }
        } else {
            db[item.id] = item
            return item
        }
    }

    override fun remove(item: TodoItem): Boolean {
        return remove(item.id)
    }

    override fun remove(id: Int): Boolean {
        return if (exists(id)) {
            db.remove(id)
            true
        } else {
            false
        }
    }

    /**
     * Metodo que irá gerar o id, ele irá consultar continuamente o map
     * até encontrar um id que não esteja presente no map.
     */
    private fun generateId(): Int {
        var id: Int

        do {
            id = Random.nextInt().absoluteValue
        } while (exists(id))

        return id
    }
}