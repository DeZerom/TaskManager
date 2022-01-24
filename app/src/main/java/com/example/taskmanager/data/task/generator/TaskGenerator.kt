package com.example.taskmanager.data.task.generator

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskmanager.data.UsableForFilteringTasks
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.data.day.DaysHandler
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.data.task.generator.strategies.ByDate
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
    private val mResult = MutableLiveData<List<Task>>()

    /**
     * Result of generate functions
     * @see generateForDay
     * @see generateForProjectExceptGenerated
     */
    val result: LiveData<List<Task>>
        get() = mResult

    private var mGeneratingStrategy: GeneratingStrategy? = null
    private var mStrategyNumber: Int = -1

    init {
        taskViewModel.allTasks.observe(lifecycle) {
            mTasks = it
            DaysHandler.isTasksOverdue(mTasks)
            mGeneratingStrategy?.filter(it)
        }
    }

    /**
     * Finds original task for given [task]. Original task has the same id and [Task.isGenerated] value
     * `false`. If given [task] has [Task.isGenerated] = `false`, [task] will be returned.
     * @return [task] if its [Task.isGenerated] equals to `false` or its original task
     */
    fun findOriginalTask(task: Task): Task {
        if (!task.isGenerated) return task

        val res = mTasks.find {
            return@find it.id == task.id && !it.isGenerated
        }

        res?: Log.e("TaskGenerator", "Could not find original task for task with id: " +
                "${task.id}; name: ${task.name} isGenerated: ${task.isGenerated}")
        return res!!
    }

    /**
     * Generates List of [Task] for specified [DayOfMonth]. Result is in [result]
     */
    fun generateForDay(day: DayOfMonth?) {
        if (day == null) {
            mResult.value = ArrayList(mTasks)
            return
        }

        if (mGeneratingStrategy == null || mStrategyNumber != Strategy.FOR_DAY_OF_MONTH.number()) {
            mGeneratingStrategy = GeneratingStrategy.byDayOfMonth(listOf(day))
            mStrategyNumber = Strategy.FOR_DAY_OF_MONTH.number()
        } else {
            mGeneratingStrategy?.filteringConditions = listOf(day)
        }

        mResult.value = mGeneratingStrategy?.filter(mTasks)
    }

    /**
     * Generates List of [Task] for specified [Project]. Result is in [result]
     */
    fun generateForProjectExceptGenerated(project: Project) {
        if (mGeneratingStrategy == null || mStrategyNumber != Strategy.FOR_PROJECT.number()) {
            mGeneratingStrategy = GeneratingStrategy.byProject(listOf(project))
            mStrategyNumber = Strategy.FOR_PROJECT.number()
        } else {
            mGeneratingStrategy?.filteringConditions = listOf(project)
        }

        mResult.value = mGeneratingStrategy?.filter(mTasks)
    }

    /**
     * Generates a List of [Task] for specified [Project] and [DayOfMonth]. Changes the [result]
     * [LiveData]
     */
    fun generateForProjectAndDayOfMonth(project: Project, dayOfMonth: DayOfMonth) {
        if (mGeneratingStrategy == null || mStrategyNumber != Strategy
                .FOR_PROJECT_AND_DAY_OF_MONTH.number()) {
            mGeneratingStrategy = GeneratingStrategy
                .byProjectAndDayOfMonth(listOf(project, dayOfMonth))
            mStrategyNumber = Strategy.FOR_PROJECT_AND_DAY_OF_MONTH.number()
        } else {
            mGeneratingStrategy?.filteringConditions = listOf(project, dayOfMonth)
        }

        mResult.value = mGeneratingStrategy?.filter(mTasks)
    }

    private enum class Strategy {
        FOR_DAY_OF_MONTH, FOR_PROJECT, FOR_PROJECT_AND_DAY_OF_MONTH;

        fun number(): Int {
            return when (this) {
                FOR_DAY_OF_MONTH -> 0
                FOR_PROJECT -> 1
                FOR_PROJECT_AND_DAY_OF_MONTH -> 2
            }
        }
    }
}