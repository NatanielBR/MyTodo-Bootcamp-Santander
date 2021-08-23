package com.natanielbr.mytodo.models

import com.natanielbr.mytodo.models.dataSource.DataSource

/**
 * Forma singleton de acessar o DataSource, ele será inicializado na classe Application
 * assim garantindo irá estar disponivel antes de qualquer consulta.
 */
object TodoItemRepository {
    lateinit var dataSource: DataSource
        private set

    fun init(dataSource: DataSource) {
        this.dataSource = dataSource
    }
}