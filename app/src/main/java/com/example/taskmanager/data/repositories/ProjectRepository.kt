package com.example.taskmanager.data.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskmanager.data.TheDatabase
import com.example.taskmanager.data.project.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProjectRepository(context: Context) {
    private val db = TheDatabase.getInstance(context)
    private val pDao = db.getProjectDao()

    /**
     * Contains all [Project]s
     */
    val allProjects: LiveData<List<Project>>
        get() = _allProjects
    private var _allProjects = MutableLiveData<List<Project>>()

    /**
     * Must be called to get initialize [allProjects]'s value.
     */
    suspend fun getAllProjects() {
        withContext(Dispatchers.IO) {
            _allProjects = pDao.getAll() as MutableLiveData<List<Project>>
        }
    }

    suspend fun addProject(project: Project) {
        withContext(Dispatchers.IO) {
            pDao.addProject(project)
        }
    }

    suspend fun updateProject(p: Project) {
        withContext(Dispatchers.IO) {
            pDao.updateProject(p)
        }
    }

    suspend fun deleteProject(p: Project) {
        withContext(Dispatchers.IO) {
            pDao.deleteProject(p)
        }
    }

}