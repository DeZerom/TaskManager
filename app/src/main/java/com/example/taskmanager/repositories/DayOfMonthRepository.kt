package com.example.taskmanager.repositories

import android.content.Context
import com.example.taskmanager.data.TheDatabase
import com.example.taskmanager.data.day.DayOfMonth

class DayOfMonthRepository(context: Context) {
    private val mDb = TheDatabase.getInstance(context)
    private val mDao = mDb.getDayOfMonthDao()

    val allDays = mDao.getAllDays()

    suspend fun addDay(day: DayOfMonth) {
        mDao.addMonthDay(day)
    }

    suspend fun updateDay(day: DayOfMonth) {
        mDao.updateMonthDay(day)
    }

    suspend fun deleteDay(day: DayOfMonth) {
        mDao.deleteMonthDay(day)
    }

}