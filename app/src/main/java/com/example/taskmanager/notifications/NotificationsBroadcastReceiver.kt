package com.example.taskmanager.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.room.CoroutinesRoom
import com.example.taskmanager.MainActivity
import com.example.taskmanager.data.repositories.TaskRepository
import com.example.taskmanager.data.task.Task
import kotlinx.coroutines.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.suspendCoroutine

class NotificationsBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?: return
        intent?: return

            var tasks = emptyList<Task>()
        GlobalScope.launch(Dispatchers.IO) {
            val repo = TaskRepository(context)
            tasks = repo.getAllAsList()
        }.invokeOnCompletion {
            tasks.forEach {
                Notifications.createNotificationForTask(context, it)
            }

            //create new alarm
            val alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            val pendingIntent = PendingIntent.getBroadcast(context, MainActivity.ALARM_REQUEST_CODE,
                intent, PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() +
                        Duration.between(LocalTime.now(), LocalTime.MAX).toMillis(),
                    pendingIntent)
        }
    }
}