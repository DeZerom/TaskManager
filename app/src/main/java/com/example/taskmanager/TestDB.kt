package com.example.taskmanager

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TestDbObject::class], version = 1)
abstract class TestDB : RoomDatabase() {
    abstract fun testDbObjectDao(): TestDbDAO
}