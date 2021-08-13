package com.example.taskmanager.data.day

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DayOfMonthDAO {
    @Query("SELECT * FROM DayOfMonth")
    fun getAllDays(): LiveData<List<DayOfMonth>>

    @Insert
    suspend fun addMonthDay(d: DayOfMonth)

    @Update
    suspend fun updateMonthDay(d: DayOfMonth)

    @Delete
    suspend fun deleteMonthDay(d: DayOfMonth)
}