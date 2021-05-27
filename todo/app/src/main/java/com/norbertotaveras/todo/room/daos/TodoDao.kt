package com.norbertotaveras.todo.room.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.norbertotaveras.todo.room.entities.TodoEntity

@Dao
interface TodoDao {

    @Query("SELECT * FROM table_todos ORDER BY id ASC")
    fun getAll(): LiveData<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todo: TodoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(todo: TodoEntity)
}