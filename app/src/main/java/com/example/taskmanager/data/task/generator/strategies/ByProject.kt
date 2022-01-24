package com.example.taskmanager.data.task.generator.strategies

import com.example.taskmanager.data.UsableForFilteringTasks
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.data.task.generator.GeneratingStrategy

class ByProject(mFilteringConditions: List<UsableForFilteringTasks>) :
    GeneratingStrategy(mFilteringConditions) {

    override fun filter(tasks: List<Task>): MutableList<Task> {
        val project = mFilteringConditions[0] as Project
        val res = ArrayList<Task>()
        tasks.forEach { task ->
            if (task.projectOwnerId == project.id) res.add(task)
        }
        return res
    }
}