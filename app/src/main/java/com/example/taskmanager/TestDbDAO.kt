package com.example.taskmanager

import androidx.room.Dao
import androidx.room.Query

@Dao
interface TestDbDAO {
    @Query("SELECT * FROM TestDbObject")
    fun getAll(): List<TestDbObject>

    @Query("SELECT * FROM TestDbObject WHERE info1 = :s")
    fun getByInfo1(s: String): List<TestDbObject>
}