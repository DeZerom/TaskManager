package com.example.taskmanager.data.task.converters

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverter {

    @TypeConverter
    fun toLocalDate(s: String): LocalDate {
        return LocalDate.parse(s)
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.toString()
    }

}