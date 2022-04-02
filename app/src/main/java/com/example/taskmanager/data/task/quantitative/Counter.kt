package com.example.taskmanager.data.task.quantitative

import com.example.taskmanager.data.task.Task
import java.time.Duration
import java.time.LocalDate

class Counter {
    companion object {
        fun countPerDayAmountOfIterations(task: Task): Int {
            val amount = task.amount
            val duration = Duration.between(LocalDate.now(), task.date).toDays()
            if (duration < 0) return amount

            val quotient = amount / duration.toInt()
            val remainder = amount % duration.toInt()

            return if (remainder == 0) quotient else quotient + 1
        }
    }
}