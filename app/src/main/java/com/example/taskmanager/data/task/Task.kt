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
    @PrimaryKey(autoGenerate = true) val id: Int,

    @ColumnInfo val name: String,

    @ColumnInfo(name = "project_owner_id") val projectOwnerId: Int,

    @ColumnInfo(defaultValue = "2000-01-01") val date: LocalDate,

    @ColumnInfo(defaultValue = "-1") val amount: Int = -1,

    @ColumnInfo(defaultValue = "1") val repeat: Int = 1
): Parcelable
{
    override fun toString(): String {
        return name
    }

    companion object {
        const val REPEAT_NEVER = 1
        const val REPEAT_EVERY_DAY = 2
    }
}
