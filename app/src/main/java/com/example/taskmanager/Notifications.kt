package com.example.taskmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.taskmanager.data.DatabaseController
import com.example.taskmanager.data.task.Task
import java.time.LocalDate

class Notifications {

    companion object {
        private var isNotificationChannelCreated = false
        private var id = 0

        private fun createNotificationChannel(context: Context) {
            if (isNotificationChannelCreated) return

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
            isNotificationChannelCreated = true
        }

        fun createNotificationsForTodayTasks(
            context: Context,
            databaseController: DatabaseController)
        {
            databaseController.taskViewModel.allTasks.value?.forEach {
                if (it.date == LocalDate.now()) createNotificationForTask(context, it)
            }
        }

        fun createNotificationForTask(context: Context, task: Task) {
            createNotificationChannel(context)
            val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(task.name)
                .setContentText(task.date.toString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(context)) {
                notify(id++, builder.build())
            }
        }

        private const val NOTIFICATION_CHANNEL_ID = "NotCh"
        private const val CHANNEL_NAME = "Task Manager"
        private const val CHANNEL_DESCRIPTION = "Description"
    }

}