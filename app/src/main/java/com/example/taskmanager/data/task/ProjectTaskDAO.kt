package com.example.taskmanager.data.task

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProjectTaskDAO {

    @Query("SELECT * FROM ProjectTask")
    fun getAll(): LiveData<List<ProjectTask>>

    @Query("SELECT * FROM ProjectTask WHERE id = :id")
    fun getById(id: Int): ProjectTask

    @Insert
    fun addProjectTask(pt: ProjectTask)

//    @Delete
//    fun deleteById(id: Int)
//
}