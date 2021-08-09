package com.example.taskmanager.data.day

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

interface MonthDayDAO {
    @Query("SELECT * FROM MonthDay")
    fun getAllDays(): LiveData<List<MonthDay>>

    @Insert
    suspend fun addMonthDay(d: MonthDay)

    @Update
    suspend fun updateMonthDay(d: MonthDay)

    @Delete
    suspend fun deleteMonthDay(d: MonthDay)
}