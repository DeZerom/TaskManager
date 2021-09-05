package com.example.taskmanager.data.day

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.taskmanager.data.UsableForFilteringTasks
import com.example.taskmanager.data.converters.BooleanConverter
import com.example.taskmanager.data.converters.LocalDateConverter
import java.time.LocalDate

@Entity
@TypeConverters(LocalDateConverter::class, BooleanConverter::class)
data class DayOfMonth(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val date: LocalDate,
    val isWeekend: Boolean
): UsableForFilteringTasks
{
    override fun toString(): String {
        return if (isWeekend) {
            "$date WEEKEND"
        } else {
            "$date WEEKDAY"
        }
    }

    override fun getCondition(): Any {
        return date
    }
}