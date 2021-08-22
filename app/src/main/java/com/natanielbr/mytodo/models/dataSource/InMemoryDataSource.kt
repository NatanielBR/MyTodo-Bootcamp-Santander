package com.natanielbr.mytodo.models.dataSource

import com.natanielbr.mytodo.models.dataSource.model.TodoItem
import kotlin.math.absoluteValue
import kotlin.random.Random

class InMemoryDataSource() : DataSource(null) {
    private val db: MutableMap<Int, TodoItem> = mutableMapOf()

    override fun getAll(): List<TodoItem> {
        return db.values.toList()
    }

    override fun get(id: Int): TodoItem? {
        return db[id]
    }

    override fun getOrCreate(item: TodoItem): TodoItem {
        return getByName(item.name) ?: insert(item)
    }

    override fun exists(id: Int): Boolean {
        return db.containsKey(id)
    }

    override fun insert(item: TodoItem): TodoItem {
        if (item.id == -1) {
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

    override fun insertAll(items: List<TodoItem>): List<TodoItem> {
        return items.map { insert(it) }
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

    override fun removeAll(ids: List<Int>): Boolean {
        return ids.map { remove(it) }.find { it } ?: false
    }


    private fun getByName(name: String): TodoItem? {
        return db.values.find { it.name == name }
    }

    private fun generateId(): Int {
        var id: Int

        do {
            id = Random.nextInt().absoluteValue
        } while (exists(id))

        return id
    }
}