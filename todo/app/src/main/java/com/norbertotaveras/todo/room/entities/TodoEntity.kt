package com.norbertotaveras.todo.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.norbertotaveras.todo.models.Priority

@Entity(tableName = "table_todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var priority: Priority,
    var description: String) {
}