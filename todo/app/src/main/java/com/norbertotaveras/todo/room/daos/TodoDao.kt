package com.norbertotaveras.todo.room.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.norbertotaveras.todo.models.Priority
import com.norbertotaveras.todo.room.entities.TodoEntity

@Dao
interface TodoDao {

    @Query("SELECT * FROM table_todos ORDER BY id ASC")
    fun getAll(): LiveData<MutableList<TodoEntity>>

    @Query("DELETE FROM table_todos")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todo: TodoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(todo: TodoEntity)

    @Update
    suspend fun update(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)

    @Query("SELECT * FROM table_todos WHERE title LIKE :query")
    fun search(query: String): LiveData<MutableList<TodoEntity>>

    @Query("SELECT * FROM table_todos ORDER BY CASE WHEN priority LIKE 'L%' THEN 1 WHEN priority LIKE 'M%' THEN 2 WHEN priority LIKE 'H%' THEN 3 END")
    fun low(): LiveData<MutableList<TodoEntity>>

    @Query("SELECT * FROM table_todos ORDER BY CASE WHEN priority LIKE 'M%' THEN 1 WHEN priority LIKE 'L%' THEN 2 WHEN priority LIKE 'H%' THEN 3 END")
    fun medium(): LiveData<MutableList<TodoEntity>>

    @Query("SELECT * FROM table_todos ORDER BY CASE WHEN priority LIKE 'H%' THEN 1 WHEN priority LIKE 'M%' THEN 2 WHEN priority LIKE 'L%' THEN 3 END")
    fun high(): LiveData<MutableList<TodoEntity>>
}