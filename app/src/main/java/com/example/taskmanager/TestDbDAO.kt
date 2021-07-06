package com.example.taskmanager

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TestDbDAO {
    @Query("SELECT * FROM TestDbObject")
    fun getAll(): List<TestDbObject>

    @Query("SELECT * FROM TestDbObject WHERE info1 = :s LIMIT 1")
    fun getByInfo1(s: String): TestDbObject

    @Insert
    fun insert(obj: TestDbObject)
}