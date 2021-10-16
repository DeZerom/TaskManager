package com.example.taskmanager.data

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.data.day.DaysHandler
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.data.viewmodels.*
import com.example.taskmanager.data.task.generator.TaskGenerator
import java.time.LocalDate
import java.time.Month

/**
 * Implementation of facade pattern. Do anything whatever needed to do with database and its
 * content.
 * @see TaskViewModel
 * @see ProjectViewModel
 * @see DayOfMonthViewModel
 * @see TaskGenerator
 */
class DatabaseController(fragment: Fragment) {
    private val mTaskViewModel: TaskViewModel
    val taskViewModel: TaskViewModel
        get() = mTaskViewModel

    private val mProjectViewModel: ProjectViewModel
    val projectViewModel: ProjectViewModel
        get() = mProjectViewModel

    private val mDayOfMonthViewModel: DayOfMonthViewModel
    val daysViewModel: DayOfMonthViewModel
        get() = mDayOfMonthViewModel

    private val mTaskGenerator: TaskGenerator
    val taskGenerator: TaskGenerator
        get() = mTaskGenerator

    private val mDaysHandler: DaysHandler
    val daysHandler: DaysHandler
        get() = mDaysHandler

    init {
        val provider = ViewModelProvider(fragment)
        mTaskViewModel = provider.get(TaskViewModel::class.java)
        mProjectViewModel = provider.get(ProjectViewModel::class.java)
        mDayOfMonthViewModel = provider.get(DayOfMonthViewModel::class.java)
        mTaskGenerator = TaskGenerator(fragment.viewLifecycleOwner, mTaskViewModel)
        mDaysHandler = DaysHandler(mDayOfMonthViewModel, fragment.viewLifecycleOwner)
    }

    fun addDay(day: DayOfMonth) {
        mDayOfMonthViewModel.addDay(day)
    }

    fun updateDay(day: DayOfMonth) {
        mDayOfMonthViewModel.updateDay(day)
    }

    fun deleteDuplicatedDays() {
        mDaysHandler.deleteDuplicates()
    }

    fun isMonthExists(month: Month): Boolean {
        return mDaysHandler.isMonthExists(month)
    }

    fun createMonth(month: Month) {
        mDaysHandler.createMonth(month)
    }

    fun deleteMonthsExcept(month: Month) {
        mDaysHandler.deleteExcept(month)
    }

    /**
     * Tries to find [DayOfMonth] with specified date. If it can't find, it will create a new one
     * with specified date and isWeekend.
     * @param date [LocalDate] which will be used to find [DayOfMonth] with equal [DayOfMonth.date]
     * @param isWeekend If [DayOfMonth] with specified [date] will not be found, this param will
     * be used to create a new one [DayOfMonth]
     */
    fun getDay(date: LocalDate, isWeekend: Boolean = false): DayOfMonth {
        val d = mDayOfMonthViewModel.allDays.value?.find { return@find it.date == date }
        d?: run {
            val dayNotNull = DayOfMonth(0, date, isWeekend)
            addDay(dayNotNull)
            return dayNotNull
        }
        return d
    }

    fun addTask(task: Task) {
        mTaskViewModel.addTask(task)
    }

    fun updateTask(task: Task) {
        mTaskViewModel.updateTask(task)
    }

    fun deleteTask(task: Task) {
        mTaskViewModel.deleteTask(task)
    }

    fun completeTask(task: Task) {
        if (task.amount > 1) {
            val t = Task.createTaskWithAnotherAmount(task, task.amount - 1)
            mTaskViewModel.updateTask(t)
        } else {
            mTaskViewModel.deleteTask(task)
        }
    }

    fun generateForDay(day: DayOfMonth) {
        mTaskGenerator.generateForDay(day)
    }

    fun generateForProjectExceptGenerated(project: Project) {
        mTaskGenerator.generateForProjectExceptGenerated(project)
    }
}
