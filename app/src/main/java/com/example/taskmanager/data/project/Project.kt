package com.example.taskmanager.data.project

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.taskmanager.data.UsableForFilteringTasks
import com.example.taskmanager.data.converters.BooleanConverter
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
@TypeConverters(BooleanConverter::class)
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo var name: String,
    @ColumnInfo var isForWeekend: Boolean = false
): Parcelable, UsableForFilteringTasks
{
    override fun toString(): String {
        return name
    }

    override fun getCondition(): Any {
        return id
    }
}