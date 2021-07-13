package com.example.taskmanager.repositories

import android.content.Context
import com.example.taskmanager.data.TheDatabase
import com.example.taskmanager.data.task.Task

class TaskRepository(context: Context) {
    private val db = TheDatabase.getInstance(context)
    private val tDao = db.getTaskDao()
    val allTasks = tDao.getAll()

    suspend fun addTask(task: Task) {
        tDao.addProjectTask(task)
    }

    suspend fun updateTask(task: Task) {
        tDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        tDao.deleteTask(task)
    }

}