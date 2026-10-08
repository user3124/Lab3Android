package com.example.todolab.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SyncScheduler {
    private const val PERIODIC_WORK_NAME = "periodic_sync"
    private const val ONE_TIME_WORK_NAME = "one_time_sync"

    // Запускать только при наличии интернета
    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    // Периодическая синхронизация раз в 15 минут (системный минимум)
    fun schedulePeriodic(context: Context) {
        val request = PeriodicWorkRequestBuilder<SyncTasksWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // не пересоздавать при каждом запуске приложения
            request
        )
    }

    // Немедленная одноразовая синхронизация (кнопка и BroadcastReceiver)
    fun syncNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<SyncTasksWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            ONE_TIME_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}