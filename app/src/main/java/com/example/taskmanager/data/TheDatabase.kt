package com.example.taskmanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.project.ProjectDAO
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.data.task.TaskDAO

@Database(entities = [Project::class, Task::class], version = 2)
abstract class TheDatabase: RoomDatabase() {

    abstract fun getProjectDao(): ProjectDAO

    abstract fun getTaskDao(): TaskDAO

    companion object {
        @Volatile private var instance: TheDatabase? = null


        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE 'Task'('id' INTEGER NOT NULL, 'name' TEXT NOT NULL, PRIMARY KEY('id'))"
                )
            }
        }

        @Synchronized
        fun getInstance(context: Context): TheDatabase {
            var tmp = instance
            return if (tmp != null) {
                tmp
            } else {
                tmp = Room.databaseBuilder(context, TheDatabase::class.java, "TheDb")
                    .addMigrations(MIGRATION_1_2)
                    .build()
                instance = tmp
                tmp
            }
        }
    }

}