package com.example.taskmanager.data.task.generator

import com.example.taskmanager.data.UsableForFilteringTasks
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.data.task.generator.strategies.ByDate
import com.example.taskmanager.data.task.generator.strategies.ByProject
import com.example.taskmanager.data.task.generator.strategies.ByProjectAndDate

abstract class GeneratingStrategy (protected var mFilteringConditions: List<UsableForFilteringTasks>) {
    open var filteringConditions: List<UsableForFilteringTasks>
        get() = mFilteringConditions
        set(value) {
            mFilteringConditions = value
        }

    abstract fun filter(tasks: List<Task>): MutableList<Task>

    companion object {
        fun byProject(project: List<Project>): GeneratingStrategy {
            return ByProject(project)
        }
        fun byDayOfMonth(dayOfMonth: List<DayOfMonth>): GeneratingStrategy {
            return ByDate(dayOfMonth)
        }
        fun byProjectAndDayOfMonth(projectAndDayOfMonth: List<UsableForFilteringTasks>)
        : GeneratingStrategy {
            return ByProjectAndDate(projectAndDayOfMonth)
        }
    }
}