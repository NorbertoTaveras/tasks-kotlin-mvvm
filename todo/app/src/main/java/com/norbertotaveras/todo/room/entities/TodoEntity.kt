package com.norbertotaveras.todo.room.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.norbertotaveras.todo.models.Priority
import kotlinx.parcelize.Parcelize
import java.text.DateFormat


@Entity(tableName = "table_todos")
@Parcelize
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var priority: Priority,
    var description: String,
    var completed: Boolean = false,
    var created: Long = System.currentTimeMillis()): Parcelable {

    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)
}