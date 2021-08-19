package com.natanielbr.mytodo.models.dataSource

import android.content.Context
import com.natanielbr.mytodo.models.dataSource.model.TodoItem
import kotlin.random.Random

class InMemoryDataSource() : DataSource(null) {
    private val db: MutableMap<Long, TodoItem> = mutableMapOf()

    init {
        // dummy data
        val now = System.currentTimeMillis()
        db[0] = TodoItem(0, "Tarefa 1", now, now)
        db[1] = TodoItem(1, "Tarefa 2", now + 60, now + 60)
        db[2] = TodoItem(2, "Tarefa 3", now + 120, now + 120)
    }

    override fun getAll(): List<TodoItem> {
        return db.values.toList()
    }

    override fun get(id: Long): TodoItem? {
        return db[id]
    }

    override fun getOrCreate(item: TodoItem): TodoItem {
        return getByName(item.name) ?: insert(item)
    }

    override fun exists(id: Long): Boolean {
        return db.containsKey(id)
    }

    override fun insert(item: TodoItem): TodoItem {
        generateId().also {
            val nItem = TodoItem(it, item.name, item.created, item.updated)
            db[it] = nItem

            return nItem
        }
    }

    override fun insertAll(items: List<TodoItem>): List<TodoItem> {
        return items.map { insert(it) }
    }

    override fun remove(item: TodoItem): Boolean {
        return remove(item.id)
    }

    override fun remove(id: Long): Boolean {
        return if (exists(id)) {
            db.remove(id)
            true
        } else {
            false
        }
    }

    override fun removeAll(ids: List<Long>): Boolean {
        return ids.map { remove(it) }.find { it } ?: false
    }


    private fun getByName(name: String): TodoItem? {
        return db.values.find { it.name == name }
    }

    private fun generateId(): Long {
        var id: Long

        do {
            id = Random.nextLong()
        } while (!exists(id))

        return id
    }
}