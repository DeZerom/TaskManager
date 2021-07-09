package com.example.taskmanager.data.project

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProjectDAO {

    @Query("SELECT * FROM Project")
    fun getAll(): LiveData<List<Project>>

    @Query("SELECT * FROM Project WHERE id = :id")
    fun getById(id: Int): Project

    @Insert
    suspend fun addProject(p: Project)

    @Update
    suspend fun updateProject(p: Project)

    @Delete
    suspend fun deleteProject(p: Project)

}