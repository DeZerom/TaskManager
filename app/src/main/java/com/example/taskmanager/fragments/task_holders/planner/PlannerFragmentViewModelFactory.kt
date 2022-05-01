package com.example.taskmanager.fragments.task_holders.planner

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.data.DatabaseController
import com.example.taskmanager.fragments.task_holders.TaskRecyclerAdapter
import javax.inject.Inject

class PlannerFragmentViewModelFactory(
    private val application: Application,
    private val databaseController: DatabaseController
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlannerFragmentViewModel(application,
            databaseController) as T
    }
}