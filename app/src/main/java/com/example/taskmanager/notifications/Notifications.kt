package com.example.taskmanager.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.taskmanager.MainActivity
import com.example.taskmanager.R
import com.example.taskmanager.data.DatabaseController
import com.example.taskmanager.data.day.DaysHandler
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

        fun createNotificationsForOverdueTasks(
            context: Context,
            databaseController: DatabaseController)
        {
            val allTasks = databaseController.taskViewModel.allTasks.value
            allTasks?: run {
                Log.e(this::class.java.toString(), "DbC's taskViewModel.allTasks.value is null")
                return
            }

            val overdue = DaysHandler.isTasksOverdue(allTasks)
            overdue.forEach {
                createNotificationForTask(context, it)
            }
        }

        fun createNotificationsForTodayTasks(
            context: Context,
            databaseController: DatabaseController)
        {
            databaseController.taskViewModel.allTasks.value?.forEach {
                if (it.date == LocalDate.now()) createNotificationForTask(context, it)
            }
        }

        fun createNotificationForTask(context: Context, task: Task, isOverdue: Boolean = false) {
            val pendingIntent = NavDeepLinkBuilder(context)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.dayFragment)
                .createPendingIntent()

            val title = if (isOverdue) context.getString(R.string.today_string) else context.getString(R.string.overdue_string)

            createNotificationChannel(context)
            val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText("${task.name} - ${task.date}")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(id++, builder.build())
            }
        }

        private const val NOTIFICATION_CHANNEL_ID = "NotCh"
        private const val CHANNEL_NAME = "Task Manager"
        private const val CHANNEL_DESCRIPTION = "Description"
    }

}