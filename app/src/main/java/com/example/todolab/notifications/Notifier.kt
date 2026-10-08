package com.example.todolab.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.todolab.MainActivity

object Notifier {
    private const val CHANNEL_ID = "tasks_channel"
    private const val ID_SYNC_DONE = 1
    private const val ID_TASK_ADDED = 2

    // minSdk = 26, поэтому канал нужен всегда
    private fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Задачи",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Уведомления о синхронизации и новых задачах"
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    // Уведомление 1: успешная фоновая синхронизация
    fun notifySyncDone(context: Context, newCount: Int) {
        show(
            context = context,
            id = ID_SYNC_DONE,
            title = "Синхронизация завершена",
            text = "Новых задач с сервера: $newCount",
            icon = android.R.drawable.stat_notify_sync
        )
    }

    // Уведомление 2: задача добавлена через ContentProvider из другого приложения
    fun notifyTaskAdded(context: Context, taskTitle: String) {
        show(
            context = context,
            id = ID_TASK_ADDED,
            title = "Новая задача из другого приложения",
            text = taskTitle,
            icon = android.R.drawable.ic_dialog_info
        )
    }

    @SuppressLint("MissingPermission")
    private fun show(context: Context, id: Int, title: String, text: String, icon: Int) {
        // На Android 13+ нужно runtime-разрешение. Если его нет, молча выходим
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        createChannel(context)

        val openApp = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, openApp,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(id, notification)
    }
}