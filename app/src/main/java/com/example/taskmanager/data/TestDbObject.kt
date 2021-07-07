package com.example.taskmanager.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TestDbObject(
@PrimaryKey(autoGenerate = true) val id: Int,
@ColumnInfo val info1: String?,
@ColumnInfo val info2: String?,
)
{
    override fun toString(): String {
        return info1 ?: ""
    }
}
