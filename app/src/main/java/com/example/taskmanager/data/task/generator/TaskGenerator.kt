package com.example.taskmanager.data.task.generator

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskmanager.data.UsableForFilteringTasks
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.data.viewmodels.TaskViewModel
import java.time.LocalDate
import java.util.*

class TaskGenerator(lifecycle: LifecycleOwner, taskViewModel: TaskViewModel) {
    /**
     * List of [Task] from database
     */
    private var mTasks: List<Task> = emptyList()

    /**
     * Internal mutable live data
     */
    private val mResult = MutableLiveData(mTasks)

    /**
     * Result of generate functions
     * @see generateForDay
     * @see generateForProjectExceptGenerated
     */
    val result: LiveData<List<Task>>
        get() = mResult

    /**
     * Last provided argument for generate functions
     */
    private var lastArgument: UsableForFilteringTasks? = null

    private var lastFunc: LastFunc? = null

    init {
        taskViewModel.allTasks.observe(lifecycle) {
            mTasks = it as MutableList<Task>
            lastArgument?.let { a -> lastFunc?.getFunc(this)?.invoke(a) }
        }
    }

    /**
     * Generates List of [Task] for specified [DayOfMonth]. Result is in [result]
     */
    fun generateForDay(day: DayOfMonth) {
        lastArgument = day
        lastFunc = LastFunc.FOR_DAY
        val res = LinkedList<Task>()
        mTasks.forEach {
            if (it.date == day.date) res.add(it)
            else if (it.repeat == Task.REPEAT_EVERY_DAY) {
                val t = generateTaskWithNewDate(it, day.date)
                mTasks.add(t) //todo
                res.add(t)
            }
            else if (it.repeat == Task.REPEAT_EVERY_DAY_EXCEPT_HOLIDAYS && !day.isWeekend) {
                val t = generateTaskWithNewDate(it, day.date)
                mTasks.add(t)
                res.add(t)
            }
        }
        mResult.value = res
    }

    /**
     * Needed only for [LastFunc.getFunc]
     */
    private fun staffGenerateForDay(day: UsableForFilteringTasks) {
        generateForDay(day.getCondition() as DayOfMonth)
    }

    /**
     * Generates List of [Task] for specified [Project]. Result is in [result]
     */
    fun generateForProjectExceptGenerated(project: Project) {
        lastArgument = project
        lastFunc = LastFunc.FOR_P_EXCEPT_G
        val res = LinkedList<Task>()
        mTasks.forEach {
            if (it.projectOwnerId == project.id) res.add(it)
        }
        mResult.value = res
    }

    /**
     * Needed only for [LastFunc.getFunc]
     */
    private fun staffGenerateForProjectExceptGenerated(project: UsableForFilteringTasks) {
        generateForProjectExceptGenerated(project.getCondition() as Project)
    }

    private fun generateTaskWithNewDate(t: Task, d: LocalDate): Task {
        val task = Task.createTaskWithAnotherDate(t, d)
        task.isGenerated = true
        return task
    }

    private enum class LastFunc {
        FOR_DAY {
            override fun getFunc(tg: TaskGenerator): (UsableForFilteringTasks) -> Unit {
                return tg::staffGenerateForDay
            }
        },
        FOR_P_EXCEPT_G {
            override fun getFunc(tg: TaskGenerator): (UsableForFilteringTasks) -> Unit {
                return tg::staffGenerateForProjectExceptGenerated
            }
        };
        abstract fun getFunc(tg: TaskGenerator): (UsableForFilteringTasks) -> Unit
    }
}