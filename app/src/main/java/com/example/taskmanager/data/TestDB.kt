package com.example.taskmanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TestDbObject::class], version = 1)
abstract class TestDB : RoomDatabase() {
    abstract fun testDbObjectDao(): TestDbDAO

    companion object {
        @Volatile
        private var instance: TestDB? = null

        fun getDb(context: Context): TestDB {
            var tmp = instance
            return if (tmp != null) tmp
            else {
                tmp = Room.databaseBuilder(
                    context.applicationContext,
                    TestDB::class.java, "test_db").build()
                instance = tmp
                tmp
            }
        }
    }
}