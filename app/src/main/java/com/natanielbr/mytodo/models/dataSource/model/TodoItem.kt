package com.natanielbr.mytodo.models.dataSource.model

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.natanielbr.mytodo.ui.services.TodoNotifier
import java.util.concurrent.TimeUnit

data class TodoItem(
    val id: Int = -1,
    var name: String, // Nome unico, o datasource irá cuidar disso
    var target: Long, // Em Epoch
    var enabled: Boolean = true // Para evitar que notifique algo que já passou
) {

    fun getUniqueName(): String {
        return "$id-$name"
    }

    fun scheduleNotification(context: Context) {
        val uniqueName = getUniqueName()
        val data = Data.Builder().putInt(uniqueName, 0).build()
        var delay = target - System.currentTimeMillis()
        if (delay < 0) delay = 0

        val notificationWork = OneTimeWorkRequest.Builder(TodoNotifier::class.java)
            .addTag(uniqueName)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()

        Log.d("neodev", "scheduleNotification: $delay - ${name}")

        val instanceWorkManager = WorkManager.getInstance(context)
        instanceWorkManager.beginUniqueWork(
            uniqueName,
            ExistingWorkPolicy.REPLACE,
            notificationWork
        ).enqueue()
    }
}