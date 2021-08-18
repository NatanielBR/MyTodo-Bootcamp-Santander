package com.natanielbr.mytodo

import android.app.Application
import com.natanielbr.mytodo.models.box.ObjectBox

class MyTodoApp : Application() {
    override fun onCreate() {
        ObjectBox.init(this)
        super.onCreate()
    }
}