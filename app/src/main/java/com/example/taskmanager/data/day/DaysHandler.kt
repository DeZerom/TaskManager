package com.example.taskmanager.data.day

import android.util.Log
import androidx.annotation.IntRange
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.data.viewmodels.DayOfMonthViewModel
import java.lang.IllegalArgumentException
import java.time.*

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

    fun getNextProperDate(task: Task): LocalDate {
        if (!task.isRepeatable) throw IllegalArgumentException("Could not get NEXT proper date for " +
                "task with id=${task.id}, because this task is not repeatable")

        return when(task.repeat) {
            Task.REPEAT_EVERY_DAY -> {
                task.date.plusDays(1)
            }
            Task.REPEAT_EVERY_DAY_EXCEPT_HOLIDAYS -> {
                //try to find proper DayOfMonth
                val d = mDays.find { return@find it.date == task.date.plusDays(1) }
                //if there is no proper DayOfMonth return
                d?: return task.date.plusDays(1)

                //if there is such DayOfMonth check if it is weekend
                var currentDayOfMonth: DayOfMonth = d
                var i: Long = 2
                while (currentDayOfMonth.isWeekend) {
                    val tmp = mDays.find { return@find it.date == task.date.plusDays(i) }
                    tmp?: return task.date.plusDays(i)

                    currentDayOfMonth = tmp
                    i++
                }

                currentDayOfMonth.date
            }
            else -> {
                throw IllegalArgumentException("Task with id=${task.id} has illegal " +
                        "Task.repeat value")
            }
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
     * Finds [DayOfMonth] corresponding to the given [date]. Returns true if that [DayOfMonth] have
     * [DayOfMonth.isWeekend] equal to `true`. Otherwise returns false. If it's impossible to find
     * corresponding [DayOfMonth] returns false
     */
    fun isCorrespondingDayOfMonthWeekend(date: LocalDate): Boolean {
        val d = mDays.find { return@find it.date == date }
        d?: run {
            Log.i("${this::class}", "Unable to find DayOfMonth with date = $date")
            return false
        }

        return d.isWeekend
    }

    /**
     * Checks if [DayOfMonth] with given date exists.
     */
    private fun isDayOfMonthExists(date: LocalDate): Boolean {
        return mDays.find { return@find it.date == date } != null
    }
}
