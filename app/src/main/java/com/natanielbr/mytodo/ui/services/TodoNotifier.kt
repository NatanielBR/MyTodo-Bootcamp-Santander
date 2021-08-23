package com.natanielbr.mytodo.ui.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager
import android.media.RingtoneManager.getDefaultUri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.natanielbr.mytodo.MainActivity
import com.natanielbr.mytodo.MyTodoApp
import com.natanielbr.mytodo.R
import com.natanielbr.mytodo.models.TodoItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val NOTIFICATION_ID = "notification_id"

/**
 * Worker que irá notificar quando chegar o dia e hora que o usuario escolheu.
 *
 * O Worker funciona "agendando" o trabalho até o tempo em que o usuario escolheu,
 * setando o delay. Isso não é feito aqui, mas na classe TodoItem.
 *
 * @see com.natanielbr.mytodo.models.dataSource.model.TodoItem
 */
class TodoNotifier(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val id = inputData.getInt(NOTIFICATION_ID, 0)

        withContext(Dispatchers.IO) {
            TodoItemRepository.dataSource.also {
                val todo = it.get(id)!!
                todo.enabled = false
                it.insert(todo)
            }
        }

        sendNotification(id)
        MyTodoApp.alarmSound.play()

        return Result.success()
    }

    private fun sendNotification(id: Int) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION_ID, id)

        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val titleNotification = applicationContext.getString(R.string.app_name)
        val notification = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.notification_channel_id)
        )
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(titleNotification)
            .setContentIntent(
                getActivity(
                    applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setDeleteIntent(
                getActivity(
                    applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setDefaults(DEFAULT_ALL).setAutoCancel(true)

        notification.priority = PRIORITY_MAX

        notification.setChannelId(applicationContext.getString(R.string.notification_channel_id))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ringtoneManager = getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttributes = AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                .setContentType(CONTENT_TYPE_SONIFICATION).build()
            val channel = NotificationChannel(
                applicationContext.getString(R.string.notification_channel_id),
                "MyTodo Service", IMPORTANCE_HIGH
            )
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }


        notificationManager.notify(id, notification.build())
    }
}