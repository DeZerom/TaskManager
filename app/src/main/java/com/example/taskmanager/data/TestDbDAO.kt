package com.example.taskmanager.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TestDbDAO {
    @Query("SELECT * FROM TestDbObject")
    fun getAll(): LiveData<List<TestDbObject>>

    @Query("SELECT * FROM TestDbObject WHERE info1 = :s LIMIT 1")
    fun getByInfo1(s: String): TestDbObject

    @Query("SELECT * FROM TestDbObject WHERE id = :id")
    fun getById(id: Int): TestDbObject

    @Insert
    fun insert(obj: TestDbObject)
}