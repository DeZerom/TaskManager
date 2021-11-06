package com.example.taskmanager

import android.util.Log
import com.example.taskmanager.data.task.Task
import java.time.LocalDate
import java.util.*

/**
 * Handles dates to mark overdue tasks
 */
class DateHandler {


    companion object {
        /**
         * Check if given [task] is overdue. If [Task.date] is bigger than [LocalDate.now] returns `true`.
         * Otherwise returns `false`.
         */
        fun isTaskOverdue(task: Task): Boolean {
            return if (task.date < LocalDate.now()) {
                Log.i(this::class.java.toString(), "Task with id = ${task.id} is overdue")
                true
            } else {
                false
            }
        }

        /**
         * Checks if list of [tasks]. Returns list of overdue tasks. If there are no overdue tasks,
         * returns an empty list.
         * @see isTaskOverdue
         */
        fun isTasksOverdue(tasks: List<Task>): List<Task> {
            val res = LinkedList<Task>()
            res.addAll(tasks.filter {
                return@filter isTaskOverdue(it)
            })

            return res
        }
    }

}