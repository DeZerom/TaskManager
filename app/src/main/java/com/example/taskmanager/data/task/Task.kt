package com.example.taskmanager.data.task

import android.os.Parcelable
import androidx.annotation.IntDef
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.converters.LocalDateConverter
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Parcelize
@Entity(foreignKeys = [ForeignKey(
    entity = Project::class,
    parentColumns = ["id"],
    childColumns = ["project_owner_id"],
    onDelete = CASCADE,
    onUpdate = CASCADE
)]
)
@TypeConverters(LocalDateConverter::class)
data class Task(
    @PrimaryKey(autoGenerate = true) var id: Int,

    @ColumnInfo var name: String,

    @ColumnInfo(name = "project_owner_id") var projectOwnerId: Int,

    @ColumnInfo(defaultValue = "2000-01-01") var date: LocalDate,

    @ColumnInfo(defaultValue = "-1") var amount: Int = -1,

    @ColumnInfo(defaultValue = "1") var repeat: Int = 1
): Parcelable
{
    val isQuantitative: Boolean
        get() = amount >= 0

    val isRepeatable: Boolean
        get() = repeat > REPEAT_NEVER

    constructor(task: Task, date: LocalDate): this(task.id, task.name, task.projectOwnerId, date,
    task.amount, task.repeat)

    constructor(task: Task, amount: Int): this(task.id, task.name, task.projectOwnerId, task.date,
    amount, task.repeat)

    override fun toString(): String {
        return name
    }

    companion object {
        const val REPEAT_NEVER = 1
        const val REPEAT_EVERY_DAY = 2
    }
}
