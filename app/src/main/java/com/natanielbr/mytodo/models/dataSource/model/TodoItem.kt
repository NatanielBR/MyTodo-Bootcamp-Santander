package com.natanielbr.mytodo.models.dataSource.model

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.natanielbr.mytodo.ui.services.NOTIFICATION_ID
import com.natanielbr.mytodo.ui.services.TodoNotifier
import java.util.concurrent.TimeUnit

/**
 * Classe no padrão do DataSource para representar um item.
 */
data class TodoItem(
    /**
     * O Id, o Datasource irá cuidar dele ser unico.
     */
    val id: Int = 0,
    /**
     * O nome do Todo_, o DataSource irá cuidar que ele seja unico.
     */
    var name: String,
    /**
     * O dia e hora que o usuario deseja receber o alarme, o tempo estará no formato Epoch.
     */
    var target: Long,
    /**
     * Para indicar que o Todo_ foi finalizado, assim evitando fazer comparações com o Target.
     */
    var enabled: Boolean = true
) {

    /**
     * Metodo gerador de um nome unico, isso é util para gerar o nome do Worker.
     * O padrão para a geração é bem simples:
     * <id>-<name>
     */
    fun getUniqueName(): String {
        return "$id-$name"
    }

    /**
     * Metodo para lançar o Worker ou substituir, caso já tenha lançado.
     */
    fun scheduleNotification(context: Context) {
        val uniqueName = getUniqueName()
        val data = Data.Builder()
            .putInt(NOTIFICATION_ID, id).build()
        var delay = target - System.currentTimeMillis()
        if (delay < 0) delay = 0

        val notificationWork = OneTimeWorkRequest.Builder(TodoNotifier::class.java)
            .addTag(uniqueName)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()

        Log.d("neodev", "scheduleNotification: $delay - $name")

        val instanceWorkManager = WorkManager.getInstance(context)
        instanceWorkManager.beginUniqueWork(
            uniqueName,
            ExistingWorkPolicy.REPLACE,
            notificationWork
        ).enqueue()
    }
}