package com.norbertotaveras.todo.room.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.norbertotaveras.todo.room.entities.TodoEntity
import com.norbertotaveras.todo.utils.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM table_todos ORDER BY id ASC")
    fun getAll(): LiveData<MutableList<TodoEntity>>

    fun getAllFlow(sortOrder: SortOrder, hideCompleted: Boolean): Flow<MutableList<TodoEntity>> =
        when (sortOrder) {
            SortOrder.BY_DATE -> date(hideCompleted)
            SortOrder.BY_NAME -> title(hideCompleted)
            SortOrder.BY_HIGH -> high(hideCompleted)
            SortOrder.BY_MEDIUM -> medium(hideCompleted)
            SortOrder.BY_LOW -> low(hideCompleted)
        }

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

    @Query("SELECT * FROM table_todos WHERE (completed != :hideCompleted or completed = 0) ORDER BY CASE WHEN priority LIKE 'L%' THEN 1 WHEN priority LIKE 'M%' THEN 2 WHEN priority LIKE 'H%' THEN 3 END")
    fun low(hideCompleted: Boolean): Flow<MutableList<TodoEntity>>

    @Query("SELECT * FROM table_todos WHERE (completed != :hideCompleted or completed = 0) ORDER BY CASE WHEN priority LIKE 'M%' THEN 1 WHEN priority LIKE 'L%' THEN 2 WHEN priority LIKE 'H%' THEN 3 END")
    fun medium(hideCompleted: Boolean): Flow<MutableList<TodoEntity>>

    @Query("SELECT * FROM table_todos WHERE (completed != :hideCompleted or completed = 0) ORDER BY CASE WHEN priority LIKE 'H%' THEN 1 WHEN priority LIKE 'M%' THEN 2 WHEN priority LIKE 'L%' THEN 3 END")
    fun high(hideCompleted: Boolean): Flow<MutableList<TodoEntity>>

    @Query("SELECT * FROM table_todos WHERE (completed != :hideCompleted or completed = 0) ORDER BY title")
    fun title(hideCompleted: Boolean): Flow<MutableList<TodoEntity>>

    @Query("SELECT * FROM table_todos WHERE (completed != :hideCompleted or completed = 0) ORDER BY created DESC")
    fun date(hideCompleted: Boolean): Flow<MutableList<TodoEntity>>

    @Query("DELETE FROM table_todos WHERE completed = 1")
    suspend fun deleteCompletedTasks()
}