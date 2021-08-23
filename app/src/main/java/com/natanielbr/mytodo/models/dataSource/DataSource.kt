package com.natanielbr.mytodo.models.dataSource

import com.natanielbr.mytodo.models.dataSource.model.TodoItem

/**
 * Representação de uma DataSource, ele contem os metodos CRUD alem de outros simples
 * como exist e getAll.
 */
abstract class DataSource {

    /**
     * Forma de obter todos os itens armazenados no DataSource.
     * A forma em que esta armazenado fica a cargo da implementação.
     */
    abstract fun getAll(): List<TodoItem>

    /**
     * Forma de obter um unico item através de seu id.
     */
    abstract fun get(id: Int): TodoItem?

    /**
     * Forma de saber se o item existe, através de seu id.
     */
    abstract fun exists(id: Int): Boolean

    /**
     * Forma de inserir ou de atualiar, caso o id seja diferente de 0, o item.
     */
    abstract fun insert(item: TodoItem): TodoItem

    /**
     * Forma conveniente de remover o item utilizando o seu objeto.
     */
    abstract fun remove(item: TodoItem): Boolean

    /**
     * Forma de remover o item, através do seu Id.
     */
    abstract fun remove(id: Int): Boolean

}