package com.example.todolab

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.todolab.receiver.NetworkReceiver
import com.example.todolab.ui.TasksScreen
import com.example.todolab.ui.theme.TodoLabTheme
import com.example.todolab.work.SyncScheduler

class MainActivity : ComponentActivity() {

    private val networkReceiver = NetworkReceiver()

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        askNotificationPermission()

        // Периодическая синхронизация каждые 15 минут (KEEP: не дублируется)
        SyncScheduler.schedulePeriodic(this)

        // Динамическая регистрация ресивера сети
        @Suppress("DEPRECATION")
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        ContextCompat.registerReceiver(
            this, networkReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED
        )

        setContent {
            TodoLabTheme {
                TasksScreen()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver)
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}