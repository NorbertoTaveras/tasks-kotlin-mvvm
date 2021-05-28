package com.norbertotaveras.todo.room.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.norbertotaveras.todo.models.Priority
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "table_todos")
@Parcelize
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var priority: Priority,
    var description: String): Parcelable {
}