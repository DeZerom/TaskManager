package com.example.taskmanager.data.task.generator.strategies

import com.example.taskmanager.data.UsableForFilteringTasks
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.data.task.generator.GeneratingStrategy
import java.time.LocalDate

class ByDate(mFilteringConditions: List<UsableForFilteringTasks>) :
    GeneratingStrategy(mFilteringConditions) {

    override fun filter(tasks: List<Task>): MutableList<Task> {
        val day = mFilteringConditions[0] as DayOfMonth
        val res = ArrayList<Task>()
        tasks.forEach {
            if (!it.doneForDays.contains(day.date) && day.date >= it.date) {
                if (it.date == day.date) res.add(it)
                else if (it.repeat == Task.REPEAT_EVERY_DAY) {
                    val t = generateTaskWithNewDate(it, day.date)
                    res.add(t)
                } else if (it.repeat == Task.REPEAT_EVERY_DAY_EXCEPT_HOLIDAYS && !day.isWeekend) {
                    val t = generateTaskWithNewDate(it, day.date)
                    res.add(t)
                }
            }
        }

        return res
    }

    /**
     * Generates new [Task] with [Task.date] equal to [d] and [Task.isGenerated] equal to `true`,
     * other fields are equal to [t] corresponding fields.
     */
    private fun generateTaskWithNewDate(t: Task, d: LocalDate): Task {
        val task = Task.createTaskWithAnotherDate(t, d)
        task.isGenerated = true
        return task
    }
}