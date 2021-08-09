package com.example.taskmanager.data.day

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.taskmanager.data.converters.LocalDateConverter
import java.time.LocalDate

@Entity
@TypeConverters(LocalDateConverter::class)
data class MonthDay(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val date: LocalDate,
    val isWeekend: Boolean
)
{
    override fun toString(): String {
        return if (isWeekend) {
            "$date WEEKEND"
        } else {
            "$date WEEKDAY"
        }
    }
}