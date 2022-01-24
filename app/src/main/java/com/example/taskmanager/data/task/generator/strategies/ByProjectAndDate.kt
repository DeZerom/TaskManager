package com.example.taskmanager.data.task.generator.strategies

import com.example.taskmanager.data.UsableForFilteringTasks
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.data.task.generator.GeneratingStrategy

class ByProjectAndDate(mFilteringConditions: List<UsableForFilteringTasks>) :
    GeneratingStrategy(mFilteringConditions) {
    private val projectFilter = ByProject(listOf(mFilteringConditions[0]))
    private val dateFilter = ByDate(listOf(mFilteringConditions[1]))

    override var filteringConditions: List<UsableForFilteringTasks>
        get() = super.filteringConditions
        set(value) {
            super.filteringConditions = value
            changeFilters()
        }

    override fun filter(tasks: List<Task>): MutableList<Task> {
        return projectFilter.filter(dateFilter.filter(tasks))
    }

    private fun changeFilters() {
        projectFilter.filteringConditions = listOf(mFilteringConditions[0])
        dateFilter.filteringConditions = listOf(mFilteringConditions[1])
    }
}