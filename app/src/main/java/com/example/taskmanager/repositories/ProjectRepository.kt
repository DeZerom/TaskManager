package com.example.taskmanager.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.taskmanager.data.TheDatabase
import com.example.taskmanager.data.project.Project

class ProjectRepository(context: Context) {
    private val db = TheDatabase.getInstance(context)
    private val pDao = db.getProjectDao()

    val allProjects: LiveData<List<Project>> = pDao.getAll()

    suspend fun addProject(project: Project) {
        pDao.addProject(project)
    }

    suspend fun updateProject(p: Project) {
        pDao.updateProject(p)
    }

    suspend fun deleteProject(p: Project) {
        pDao.deleteProject(p)
    }

}