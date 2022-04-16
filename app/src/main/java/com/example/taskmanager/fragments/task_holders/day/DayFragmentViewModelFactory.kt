package com.example.taskmanager.fragments.task_holders.day

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.data.DatabaseController

class DayFragmentViewModelFactory(
    val databaseController: DatabaseController,
    val application: Application
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DayFragmentViewModel::class.java)) {
            return DayFragmentViewModel(application, databaseController) as T
        } else {
            Log.e("DayFragmentViewModelFactory", "wrong argument type")
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}