package com.natanielbr.mytodo

import android.app.Application
import android.media.Ringtone
import android.media.RingtoneManager
import com.natanielbr.mytodo.models.TodoItemRepository
import com.natanielbr.mytodo.models.dataSource.InMemoryDataSource
import com.natanielbr.mytodo.models.dataSource.ObjectBoxDataSource
import com.natanielbr.mytodo.models.dataSource.box.ObjectBox

class MyTodoApp : Application() {
    companion object {
        lateinit var alarmSound: Ringtone
            private set
    }

    override fun onCreate() {
        TodoItemRepository.init(ObjectBoxDataSource(this))
        alarmSound = RingtoneManager.getRingtone(
            applicationContext,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        )
        super.onCreate()
    }
}