package com.example.composetutorial

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager

class NotificationHelper (private val activity: Activity){

    fun requestPermission(launcher: ActivityResultLauncher<String>) {
        if (ActivityCompat.checkSelfPermission(
                activity.applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            createNotification("Notification", "Notifications already enabled!")
        }
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("channel_id", "channel_1", importance).apply {
            description = "Notification channel"
        }
        val manager = activity.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun createNotification(title: String, content: String) {
        //TODO
    }


}