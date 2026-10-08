package com.example.todolab.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Хранит время последней успешной синхронизации в SharedPreferences
object SyncPrefs {
    private const val PREFS_NAME = "sync_prefs"
    private const val KEY_LAST_SYNC = "last_sync_time"

    private val _lastSync = MutableStateFlow(0L)
    val lastSync: StateFlow<Long> = _lastSync

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Загружаем сохранённое значение при старте приложения
    fun load(context: Context) {
        _lastSync.value = prefs(context).getLong(KEY_LAST_SYNC, 0L)
    }

    fun save(context: Context, time: Long = System.currentTimeMillis()) {
        prefs(context).edit().putLong(KEY_LAST_SYNC, time).apply()
        _lastSync.value = time
    }
}