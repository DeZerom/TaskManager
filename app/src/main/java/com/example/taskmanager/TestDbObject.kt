package com.example.taskmanager

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TestDbObject(
@PrimaryKey val id: Int,
@ColumnInfo val info1: String?,
@ColumnInfo val info2: String?
)
