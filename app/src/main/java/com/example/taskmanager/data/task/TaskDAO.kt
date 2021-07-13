package com.example.taskmanager.data.task

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDAO {

    @Query("SELECT * FROM Task")
    fun getAll(): LiveData<List<Task>>

    @Query("SELECT * FROM Task WHERE id = :id")
    fun getById(id: Int): Task

    @Insert
    suspend fun addProjectTask(t: Task)

    @Update
    suspend fun updateTask(t: Task)

    @Delete
    suspend fun deleteTask(t: Task)
}