package com.example.taskmanager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.repositories.DayOfMonthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DayOfMonthViewModel(application: Application): AndroidViewModel(application) {
    private val mRepository = DayOfMonthRepository(application)

    /**
     * Contains all days that are in the data base
     */
    val allDays = mRepository.allDays

    /**
     * Calls [DayOfMonthRepository.addDay] in the IO thread.
     * @param day the [DayOfMonth] instance to provide as an argument
     * @see [DayOfMonthRepository.addDay]
     * @see Dispatchers.IO
     */
    fun addDay(day: DayOfMonth) {
        viewModelScope.launch(Dispatchers.IO) {
            mRepository.addDay(day)
        }
    }

    /**
     * Calls [DayOfMonthRepository.updateDay] in the IO thread.
     * @param day the [DayOfMonth] instance to provide as an argument
     * @see [DayOfMonthRepository.updateDay]
     * @see [Dispatchers.IO]
     */
    fun updateDay(day: DayOfMonth) {
        viewModelScope.launch(Dispatchers.IO) {
            mRepository.updateDay(day)
        }
    }

    /**
     * Calls [DayOfMonthRepository.deleteDay] in the IO thread.
     * @param day the [DayOfMonth] to provide as an argument.
     * @see [DayOfMonthRepository.deleteDay]
     * @see [Dispatchers.IO]
     */
    fun deleteDay(day: DayOfMonth) {
        viewModelScope.launch(Dispatchers.IO) {
            mRepository.deleteDay(day)
        }
    }

}