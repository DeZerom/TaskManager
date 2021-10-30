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

    private var mWhenDaysLoaded = {}
    private var mIsDaysLoaded = false
    val isDaysLoaded: Boolean
        get() = mIsDaysLoaded
    /**
     * Called when [List] of [DayOfMonth] firstly loaded from the DB.
     */
    var whenDaysLoaded: () -> Unit
        get() = mWhenDaysLoaded
        set(value) {
            mWhenDaysLoaded = value
        }

    private lateinit var mProjects: List<Project>

    init {
        val provider = ViewModelProvider(fragment)
        mTaskViewModel = provider.get(TaskViewModel::class.java)
        mProjectViewModel = provider.get(ProjectViewModel::class.java)
        mDayOfMonthViewModel = provider.get(DayOfMonthViewModel::class.java)
        mTaskGenerator = TaskGenerator(fragment.viewLifecycleOwner, mTaskViewModel)
        mDaysHandler = DaysHandler(mDayOfMonthViewModel, fragment.viewLifecycleOwner)

        mProjectViewModel.allProjects.observe(fragment.viewLifecycleOwner) {
            mProjects = it
        }

        mDayOfMonthViewModel.allDays.observe(fragment.viewLifecycleOwner) {
            mWhenDaysLoaded.invoke()
            mIsDaysLoaded = true
        }
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

    fun findParentProject(task: Task): Project {
        return findParentProject(task.projectOwnerId)
    }

    fun findParentProject(parentProjectId: Int): Project {
        val p = mProjects.find { return@find it.id == parentProjectId }
        p?: run {
            throw NullPointerException("${this::class} can't find project with id = $parentProjectId")
        }

        return p
    }

    /**
     * If [task]'s [Task.date] is weekend and [task]'s project has [Project.isForWeekend] = `false`
     * or if it is unable to find [task]'s project returns `false`. Otherwise returns true.
     * @see DaysHandler.isCorrespondingDayOfMonthWeekend
     */
    private fun checkTaskDate(task: Task): Boolean {
        val p = findParentProject(task)

        return !(mDaysHandler.isCorrespondingDayOfMonthWeekend(task.date) && !p.isForWeekend)
    }

    /**
     * Adds [task], but checks it with [checkTaskDate] before. Returns `true` and adds [task],
     * if [checkTaskDate] returns true. If [checkTaskDate] returns `false`, it returns `false` and
     * doesn't update [task].
     */
    fun addTask(task: Task): Boolean {
        if (!checkTaskDate(task)) return false

        mTaskViewModel.addTask(task)
        return true
    }

    /**
     * Checks [task] with [checkTaskDate]. If [checkTaskDate] returns `true`, it updates [task] and
     * returns `true`. If [checkTaskDate] returns `false', it returns '`false` and doesn't update
     * [task].
     */
    fun updateTask(task: Task): Boolean {
        if (!checkTaskDate(task)) return false

        mTaskViewModel.updateTask(task)
        return true
    }

    /**
     * Deletes given [task] from the DB if it needed. Deletes only if
     * [Task.repeat] == [Task.REPEAT_NEVER]. Otherwise finds next proper date for the [task] and
     * updates it in the DB.
     */
    private fun deleteTaskIfNeeded(task: Task) {
        if (task.repeat == Task.REPEAT_NEVER) {
            mTaskViewModel.deleteTask(task)
        } else {
            if (task.isGenerated) {
                val origin = mTaskGenerator.findOriginalTask(task)
                origin.doneForDays.add(task.date)
                updateTask(origin)
            } else {
                var d = mDaysHandler.getNextProperDate(task)
                var t = Task.createTaskWithAnotherDate(task, d)
                //clearing doneForDays list
                while (task.doneForDays.remove(d)) {
                    d = mDaysHandler.getNextProperDate(t)
                    t = Task.createTaskWithAnotherDate(t, d)
                }
                updateTask(t)
            }
        }
    }

    fun deleteTask(task: Task) {
        mTaskViewModel.deleteTask(task)
    }

    fun completeTask(task: Task) {
        if (task.amount > 1) {
            val t = Task.createTaskWithAnotherAmount(task, task.amount - 1)
            mTaskViewModel.updateTask(t)
        } else {
            deleteTaskIfNeeded(task)
        }
    }

    fun generateForDay(day: DayOfMonth) {
        mTaskGenerator.generateForDay(day)
    }

    fun generateForProjectExceptGenerated(project: Project) {
        mTaskGenerator.generateForProjectExceptGenerated(project)
    }
}
