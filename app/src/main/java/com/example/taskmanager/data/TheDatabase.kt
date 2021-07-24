package com.example.taskmanager.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.project.ProjectDAO
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.data.task.TaskDAO

@Database(entities = [Project::class, Task::class], version = 3)
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

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE Task RENAME TO tmp_Task"
                )
                database.execSQL(
                    "CREATE TABLE 'Task'('id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 'name' TEXT NOT NULL)"
                )
                database.execSQL(
                    "INSERT INTO Task(id, name) SELECT id, name FROM tmp_Task"
                )
                database.execSQL(
                    "DROP TABLE tmp_Task"
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                instance = tmp
                tmp
            }
        }
    }

}