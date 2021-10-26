package com.example.taskmanager.data.converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.util.*

class ListLocalDateConverter {
    private val ldConv = LocalDateConverter()

    @TypeConverter
    fun fromList(list: List<LocalDate>): String {
        val sb = StringBuffer()
        list.forEach {
            sb.append(ldConv.fromLocalDate(it)).append(";")
        }

        return sb.toString()
    }

    @TypeConverter
    fun toList(s: String): MutableList<LocalDate> {

        val parts = s.split(";")
        val res = LinkedList<LocalDate>()
        parts.forEach {
            if (it.isBlank()) return@forEach
            res.add(ldConv.toLocalDate(it))
        }

        return res
    }

}