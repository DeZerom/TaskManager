package com.example.taskmanager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [TestDbObject::class], version = 1)
abstract class TestDB : RoomDatabase() {
    abstract fun testDbObjectDao(): TestDbDAO

    companion object {
        @Volatile
        private var instance: TestDB? = null

        fun getDb(context: Context): TestDB? {
            return if (instance != null) instance
            else {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    TestDB::class.java, "test_db").build()
                instance
            }
        }
    }
}