package com.example.taskmanager.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskmanager.data.day.DayOfMonth
import com.example.taskmanager.data.day.DayOfMonthDAO
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.project.ProjectDAO
import com.example.taskmanager.data.task.Task
import com.example.taskmanager.data.task.TaskDAO
import java.time.MonthDay

@Database(entities = [Project::class, Task::class, DayOfMonth::class], version = 11)
abstract class TheDatabase: RoomDatabase() {

    abstract fun getProjectDao(): ProjectDAO

    abstract fun getTaskDao(): TaskDAO

    abstract fun getDayOfMonthDao(): DayOfMonthDAO

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

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "DELETE FROM Project"
                )

                database.execSQL(
                    "DROP TABLE Task"
                )

                database.execSQL(
                    "CREATE TABLE Task(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \nname TEXT NOT NULL, \nproject_owner_id INTEGER NOT NULL,\nFOREIGN KEY (project_owner_id) REFERENCES Project(id)\nON DELETE CASCADE\nON UPDATE CASCADE)"
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE Task ADD date TEXT NOT NULL DEFAULT '2000-01-01'"
                )
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS DayOfMonth(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, \ndate TEXT NOT NULL, \nisWeekend INTEGER NOT NULL)"
                )
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE Task ADD amount INTEGER NOT NULL DEFAULT -1"
                )
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE Task ADD repeat INTEGER NOT NULL DEFAULT 1"
                )
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE Project ADD isForWeekend INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE Task ADD doneForDays TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE Task ADD endDate TEXT NOT NULL DEFAULT '1970-01-01'"
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5,
                    MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10,
                    MIGRATION_10_11)
                    .build()
                instance = tmp
                tmp
            }
        }
    }

}