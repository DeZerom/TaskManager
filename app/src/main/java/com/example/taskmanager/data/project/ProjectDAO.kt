package com.example.taskmanager.data.project

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProjectDAO {

    @Query("SELECT * FROM Project")
    fun getAll(): LiveData<List<Project>>

    @Query("SELECT * FROM Project WHERE id = :id")
    fun getById(id: Int): Project

    @Insert
    fun addProject(p: Project)

//    @Delete
//    fun deleteById(id: Int)

}