package com.example.taskmanager.data.task

import android.os.Parcelable
import androidx.annotation.IntDef
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.example.taskmanager.data.converters.ListLocalDateConverter
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
@TypeConverters(LocalDateConverter::class, ListLocalDateConverter::class)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int,

    @ColumnInfo val name: String,

    @ColumnInfo(name = "project_owner_id") val projectOwnerId: Int,

    @ColumnInfo(defaultValue = "2000-01-01") val date: LocalDate,

    @ColumnInfo(defaultValue = "-1") val amount: Int = -1,

    @ColumnInfo(defaultValue = "1") val repeat: Int = 1,

    @ColumnInfo(defaultValue = "") val doneForDays: List<LocalDate> = emptyList(),
): Parcelable
{
    @Ignore var isGenerated = false
    val isQuantitative: Boolean
        get() = amount >= 0

    val isRepeatable: Boolean
        get() = repeat > REPEAT_NEVER

    override fun toString(): String {
        return "$id $name $isGenerated"
    }

    companion object {
        const val REPEAT_NEVER = 1
        const val REPEAT_EVERY_DAY = 2
        const val REPEAT_EVERY_DAY_EXCEPT_HOLIDAYS = 3

        fun createTaskWithAnotherId(task: Task, newId: Int): Task {
            return Task(newId, task.name, task.projectOwnerId, task.date, task.amount,
                task.repeat, task.doneForDays)
        }

        fun createTaskWithAnotherProjectOwner(task: Task, projectOwnerId: Int): Task {
            return Task(task.id, task.name, projectOwnerId, task.date, task.amount,
                task.repeat, task.doneForDays)
        }

        fun createTaskWithAnotherProjectOwner(task: Task, project: Project): Task {
            return createTaskWithAnotherProjectOwner(task, project.id)
        }

        fun createTaskWithAnotherDate(task: Task, date: LocalDate): Task {
            return Task(task.id, task.name, task.projectOwnerId, date, task.amount,
                task.repeat, task.doneForDays)
        }

        fun createTaskWithAnotherAmount(task: Task, amount: Int): Task {
            return Task(task.id, task.name, task.projectOwnerId, task.date, amount,
                task.repeat, task.doneForDays)
        }
    }
}
