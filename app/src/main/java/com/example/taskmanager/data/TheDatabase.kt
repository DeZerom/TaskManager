package com.example.taskmanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.project.ProjectDAO
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [Project::class], version = 1)
abstract class TheDatabase: RoomDatabase() {

    abstract fun getProjectDao(): ProjectDAO

    companion object {
        @Volatile private var instance: TheDatabase? = null

        @Synchronized
        fun getInstance(context: Context): TheDatabase {
            var tmp = instance
            return if (tmp != null) {
                tmp
            } else {
                tmp = Room.databaseBuilder(context, TheDatabase::class.java, "TheDb").build()
                instance = tmp
                tmp
            }
        }
    }

}