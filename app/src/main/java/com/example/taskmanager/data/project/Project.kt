package com.example.taskmanager.data.project

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val name: String
): Parcelable
{
    override fun toString(): String {
        return name
    }
}