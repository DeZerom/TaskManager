package com.example.taskmanager.data.day

import androidx.annotation.IntRange
import com.example.taskmanager.viewmodels.DayOfMonthViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.MonthDay
import java.time.temporal.TemporalAccessor

class DaysHandler(private val mDaysViewModel: DayOfMonthViewModel) {

    private val now = LocalDate.now()

    fun createMonth() {
        createMonth(now.month)
    }

    fun createMonth(@IntRange(from = 1, to = 12) month: Int) {

    }

    fun createMonth(month: Month) {

    }

}
