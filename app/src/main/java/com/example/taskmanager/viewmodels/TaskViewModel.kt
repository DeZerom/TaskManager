package com.example.taskmanager.viewmodels

import android.app.Application
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

    fun getTasksByDate(date: LocalDate): LiveData<List<Task>> {
        //get all?
        val tasks = allTasks.value
        tasks ?: return object : LiveData<List<Task>>() {}

        val mutList = LinkedList<Task>()
        for (t: Task in tasks) {
            if (t.date == date) mutList.add(t)
            if (t.repeat == Task.REPEAT_EVERY_DAY) mutList.add(Task(t.id, t.name, t.projectOwnerId,
                date, t.amount, t.repeat))
        }

        return object : LiveData<List<Task>>(mutList) {}
    }

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
        if (task.amount > 1) updateTask(Task(task, task.amount - 1))
        else deleteTask(task)
    }

}
