package com.natanielbr.mytodo.models.box

import android.content.Context
import com.natanielbr.mytodo.models.box.entitys.MyObjectBox
import com.natanielbr.mytodo.models.box.entitys.TodoItemEntity
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