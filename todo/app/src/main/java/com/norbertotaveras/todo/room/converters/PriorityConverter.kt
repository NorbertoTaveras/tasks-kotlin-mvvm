package com.norbertotaveras.todo.room.converters

import androidx.room.TypeConverter
import com.norbertotaveras.todo.models.Priority

class PriorityConverter {
    @TypeConverter
    fun from(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun to(priority: String): Priority {
        return Priority.valueOf(priority)
    }
}