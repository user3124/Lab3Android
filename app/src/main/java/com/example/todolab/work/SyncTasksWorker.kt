package com.example.todolab.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todolab.data.AppDatabase
import com.example.todolab.data.SyncPrefs
import com.example.todolab.data.remote.ApiFactory
import com.example.todolab.data.remote.toTask
import com.example.todolab.notifications.Notifier
import kotlinx.coroutines.CancellationException

class SyncTasksWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        // Серверные id сдвигаем, чтобы они не пересеклись с локальными (1, 2, 3...)
        const val REMOTE_ID_OFFSET = 100_000L
    }

    override suspend fun doWork(): Result {
        return try {
            val dao = AppDatabase.getInstance(applicationContext).taskDao()

            // 1. Скачиваем задачи с сервера
            val remoteTasks = ApiFactory.tasksApi.getTodos()

            // 2. Добавляем только те, которых ещё нет (чтобы не затирать галочки пользователя)
            val existingIds = dao.getAll().map { it.id }.toSet()
            val newTasks = remoteTasks
                .map { it.toTask().copy(id = REMOTE_ID_OFFSET + it.id) }
                .filter { it.id !in existingIds }
            dao.insertAll(newTasks)

            // 3. Запоминаем время успешной синхронизации
            SyncPrefs.save(applicationContext)

            // 4. Уведомление об успехе
            Notifier.notifySyncDone(applicationContext, newTasks.size)

            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Нет сети, ошибка сервера и т.д.: WorkManager повторит попытку позже
            Result.retry()
        }
    }
}