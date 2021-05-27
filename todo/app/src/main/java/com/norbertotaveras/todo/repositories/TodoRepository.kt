package com.norbertotaveras.todo.repositories

import androidx.lifecycle.LiveData
import com.norbertotaveras.todo.room.daos.TodoDao
import com.norbertotaveras.todo.room.entities.TodoEntity

class TodoRepository(private val todoDao: TodoDao) {
    val getAll: LiveData<List<TodoEntity>> = todoDao.getAll()

    suspend fun insert(todo: TodoEntity) {
        todoDao.insert(todo)
    }
}