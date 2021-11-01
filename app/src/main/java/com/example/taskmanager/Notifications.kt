package com.example.taskmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService

class Notifications {

    companion object {
        private fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = CHANNEL_NAME
                val descriptionText = CHANNEL_DESCRIPTION
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    context.getSystemService(NotificationManager::class.java)

                notificationManager.createNotificationChannel(channel)
            }
        }

        fun createNotification(context: Context) {
            createNotificationChannel(context)
            val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Title")
                .setContentText("Lorem ipsum")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(context)) {
                notify(0, builder.build())
            }
        }

        private const val NOTIFICATION_CHANNEL_ID = "NotCh"
        private const val CHANNEL_NAME = "Task Manager"
        private const val CHANNEL_DESCRIPTION = "Description"
    }

}