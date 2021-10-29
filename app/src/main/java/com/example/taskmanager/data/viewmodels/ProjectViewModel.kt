package com.example.taskmanager.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.repositories.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

class ProjectViewModel(application: Application): AndroidViewModel(application) {
    private val repository = ProjectRepository(application.applicationContext)

    val allProjects = repository.allProjects

    fun addProject(project: Project) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addProject(project)
        }
    }

    fun updateProject(project: Project) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProject(project)
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProject(project)
        }
    }

    fun getListOfAllProjects(): List<Project> {
        var res = emptyList<Project>()
        viewModelScope.launch(Dispatchers.IO) {
            res = repository.getListOfProjects()
        }
        return res
    }

}
