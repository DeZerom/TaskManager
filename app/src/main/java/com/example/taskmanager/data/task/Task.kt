package com.example.taskmanager.data.task

import android.os.Parcelable
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.task.converters.LocalDateConverter
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
    @PrimaryKey(autoGenerate = true) val id: Int,

    @ColumnInfo val name: String,

    @ColumnInfo(name = "project_owner_id") val projectOwnerId: Int,

    @ColumnInfo(defaultValue = "2000-01-01") val date: LocalDate
): Parcelable
{
    override fun toString(): String {
        return name
    }
}
