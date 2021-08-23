package com.natanielbr.mytodo.models.dataSource.box

import android.content.Context
import com.natanielbr.mytodo.models.dataSource.box.entitys.MyObjectBox
import io.objectbox.BoxStore

object ObjectBox {
    lateinit var store: BoxStore
        private set

    /**
     * Inicializa o ObjectBox. Ele recomenda o uso de applicationContext
     * por que ele foi planejado para ser encerrado quando a aplicação
     * se encerrar.
     */
    fun init(context: Context) {
        store = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()
    }

}