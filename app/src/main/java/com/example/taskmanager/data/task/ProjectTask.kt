package com.example.taskmanager.data.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProjectTask(
    @PrimaryKey val id: Int,
    @ColumnInfo val name: String
)
{
    override fun toString(): String {
        return name
    }
}
