package com.example.taskmanager.data.task

import androidx.lifecycle.LifecycleOwner
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.viewmodels.TaskViewModel
import java.util.*

class TaskGenerator(lifecycle: LifecycleOwner, private val mTaskViewModel: TaskViewModel) {
    private var mTasks: List<Task> = emptyList()

    init {
        mTaskViewModel.allTasks.observe(lifecycle) {
            mTasks = it
        }
    }

    fun generateForDay(day: DayOfMonth): List<Task> {
        val res = LinkedList<Task>()
        mTasks.forEach {
            if (it.date == day.date) res.add(it)
            else if (it.repeat == Task.REPEAT_EVERY_DAY) res.add(it)
            else if (it.repeat == Task.REPEAT_EVERY_DAY_EXCEPT_HOLIDAYS && !day.isWeekend) res.add(it)
        }
        return res
    }

}