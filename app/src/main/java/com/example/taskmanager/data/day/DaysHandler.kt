package com.example.taskmanager.data.day

import androidx.annotation.IntRange
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.taskmanager.viewmodels.DayOfMonthViewModel
import java.lang.NullPointerException
import java.time.*
import java.time.temporal.TemporalAccessor

/**
 * Will work properly only if [Lifecycle.State] is at least [Lifecycle.State.RESUMED]
 */
class DaysHandler(
    private val mDaysViewModel: DayOfMonthViewModel,
    lifecycleOwner: LifecycleOwner
) {
    private val now = LocalDate.now()
    private var mDays = emptyList<DayOfMonth>()

    init {
        mDaysViewModel.allDays.observe(lifecycleOwner) {
            mDays = it
        }
    }

    /**
     * Creates all [DayOfMonth] for [Month] defined by [LocalDate.now]
     */
    fun createMonth() {
        createMonth(now.month)
    }

    /**
     * Creates all [DayOfMonth] for [Month] with given number
     */
    fun createMonth(@IntRange(from = 1, to = 12) month: Int) {
        createMonth(Month.of(month))
    }

    /**
     * Creates all [DayOfMonth] for given [Month]
     */
    fun createMonth(month: Month) {
        for (i in 1..month.length(now.isLeapYear)) {
            val date = LocalDate.of(now.year, month, i)
            mDaysViewModel.addDay(DayOfMonth(0, date, false))
        }
    }

    /**
     * Checks if all [DayOfMonth] for [Month] defined by [LocalDate.now] exist
     */
    fun isMonthExists(): Boolean {
        return isMonthExists(now.month)
    }

    /**
     * Checks if all [DayOfMonth] for [Month] with given number exist
     */
    fun isMonthExists(@IntRange(from = 1, to = 12) month: Int): Boolean {
        return isMonthExists(Month.of(month))
    }

    /**
     * Checks if all [DayOfMonth] for given [Month] exist
     */
    fun isMonthExists(month: Month): Boolean {
        for (i in 1..month.length(now.isLeapYear)) {
            val date = LocalDate.of(now.year, month, i)
            if (!isDayOfMonthExists(date)) return false
        }

        return true
    }

    /**
     * Deletes all [DayOfMonth] that is not in [Month] defined by [LocalDate.now].
     */
    fun deleteExcept() {
        deleteExcept(now.month)
    }

    /**
     * Deletes all [DayOfMonth] that is not in [Month] with given number.
     */
    fun deleteExcept(@IntRange(from = 1, to = 12) month: Int) {
        deleteExcept(Month.of(month))
    }

    /**
     * Deletes all [DayOfMonth] that is not in given [Month].
     */
    fun deleteExcept(month: Month) {
        for (d in mDays) {
            if (d.date.month != month) mDaysViewModel.deleteDay(d)
        }
    }

    /**
     * Deletes all [DayOfMonth] that duplicate other [DayOfMonth.date]. After calling it, all
     * instances of [DayOfMonth] will have unique [DayOfMonth.date].
     */
    fun deleteDuplicates() {
        for (d in mDays) {
            //find all days with d.date
            val days: MutableList<DayOfMonth> = mDays.filter { return@filter it.date == d.date }
                    as MutableList<DayOfMonth>

            //delete extras if they exist
            if (days.size > 1) {
                //save one by deleting it from the list of days to delete
                days.removeAt(0)
                //delete others
                for (d in days) mDaysViewModel.deleteDay(d)
            }
        }
    }

    /**
     * Checks if [DayOfMonth] with given date exists.
     */
    private fun isDayOfMonthExists(date: LocalDate): Boolean {
        return mDays.find { return@find it.date == date } != null
    }
}
