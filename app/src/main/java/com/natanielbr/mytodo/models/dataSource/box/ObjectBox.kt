package com.natanielbr.mytodo.models.dataSource.box

import android.content.Context
import com.natanielbr.mytodo.models.dataSource.box.entitys.MyObjectBox
import io.objectbox.BoxStore

object ObjectBox {
    lateinit var store: BoxStore
        private set

    fun init(context: Context) {
        store = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()
    }

}