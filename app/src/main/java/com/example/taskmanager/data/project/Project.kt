package com.example.taskmanager.data.project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val name: String
)
{
    override fun toString(): String {
        return name
    }
}