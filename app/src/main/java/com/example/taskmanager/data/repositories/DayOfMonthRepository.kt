package com.example.taskmanager.data.repositories

import android.content.Context
import com.example.taskmanager.data.TheDatabase
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.data.day.DayOfMonthDAO

class DayOfMonthRepository(context: Context) {
    private val mDb = TheDatabase.getInstance(context)
    private val mDao = mDb.getDayOfMonthDao()

    /**
     * Contains all days that are in the data base
     */
    val allDays = mDao.getAllDays()

    /**
     * @see [DayOfMonthDAO.addMonthDay]
     */
    suspend fun addDay(day: DayOfMonth) {
        mDao.addMonthDay(day)
    }

    /**
     * @see DayOfMonthDAO.updateMonthDay
     */
    suspend fun updateDay(day: DayOfMonth) {
        mDao.updateMonthDay(day)
    }

    /**
     * @see DayOfMonthDAO.deleteMonthDay
     */
    suspend fun deleteDay(day: DayOfMonth) {
        mDao.deleteMonthDay(day)
    }

}