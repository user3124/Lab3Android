package com.example.todolab.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.todolab.work.SyncScheduler

class NetworkReceiver : BroadcastReceiver() {

    // null = ещё не знаем предыдущее состояние
    private var wasOnline: Boolean? = null

    override fun onReceive(context: Context, intent: Intent) {
        val online = isOnline(context)

        // Синхронизируем только в момент ВОССТАНОВЛЕНИЯ сети (был офлайн, стал онлайн)
        if (online && wasOnline == false) {
            SyncScheduler.syncNow(context)
        }
        wasOnline = online
    }

    private fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(ConnectivityManager::class.java)
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}