package com.example.taskmanager.data.project

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProjectViewModel(application: Application): AndroidViewModel(application) {
    private val repository = ProjectRepository(application.applicationContext)

    val allProjects = repository.allProjects

    fun addProject(project: Project) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addProject(project)
        }
    }

}
