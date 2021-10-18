package com.example.taskmanager.data.converters

import androidx.room.TypeConverter
import java.time.LocalDate

class ListLocalDateConverter {
    val ldConv = LocalDateConverter()

    @TypeConverter
    fun fromList(list: List<LocalDate>): String {
        val sb = StringBuffer()
        list.forEach {
            sb.append(ldConv.fromLocalDate(it)).append(";")
        }

        return sb.toString()
    }

    @TypeConverter
    fun toList(s: String): List<LocalDate> {
        val parts = s.split(";")
        val res = ArrayList<LocalDate>(parts.size)
        parts.forEach {
            res.add(ldConv.toLocalDate(it))
        }

        return res
    }

}