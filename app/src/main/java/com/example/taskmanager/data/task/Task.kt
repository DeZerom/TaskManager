package com.example.taskmanager.data.task

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.example.taskmanager.data.project.Project
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(foreignKeys = [ForeignKey(
    entity = Project::class,
    parentColumns = ["id"],
    childColumns = ["project_owner_id"],
    onDelete = CASCADE,
    onUpdate = CASCADE
)]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo(name = "project_owner_id") val projectOwnerId: Int
): Parcelable
{
    override fun toString(): String {
        return name
    }
}
