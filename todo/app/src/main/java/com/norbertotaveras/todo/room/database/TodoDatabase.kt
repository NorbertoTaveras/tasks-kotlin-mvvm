package com.norbertotaveras.todo.room.database

import android.content.Context
import androidx.room.*
import com.norbertotaveras.todo.room.converters.PriorityConverter
import com.norbertotaveras.todo.room.daos.TodoDao
import com.norbertotaveras.todo.room.entities.TodoEntity

@Database(entities = [TodoEntity::class], version = 3, exportSchema = false)
@TypeConverters(PriorityConverter::class)
abstract class TodoDatabase: RoomDatabase() {

    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null

        fun getInstance(context: Context): TodoDatabase {
            val temp = INSTANCE

            if (temp != null)
                return temp

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    TodoDatabase::class.java,
                    "todo.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return INSTANCE as TodoDatabase
            }
        }
    }
}