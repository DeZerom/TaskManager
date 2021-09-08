package com.example.taskmanager.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.repositories.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*

class TaskViewModel(application: Application): AndroidViewModel(application) {
    private val repository = TaskRepository(application.applicationContext)

    val allTasks = repository.allTasks

    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(task)
        }
    }

    fun completeTask(task: Task) {
        //decrease amount or delete task
        if (task.amount > 1) {
            task.amount--
            updateTask(task)
        }
        else deleteTask(task)
    }

}
