package com.example.taskmanager.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.taskmanager.data.task.Task
import java.time.LocalDate

class NotificationsBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val task = Task(-1, "test", -1, LocalDate.now())
        context?: return

        Notifications.createNotificationForTask(context, task)
    }
}