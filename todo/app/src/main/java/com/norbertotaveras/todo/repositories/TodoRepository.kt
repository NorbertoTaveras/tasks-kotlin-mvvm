package com.norbertotaveras.todo.repositories

import androidx.lifecycle.LiveData
import com.norbertotaveras.todo.models.Priority
import com.norbertotaveras.todo.room.daos.TodoDao
import com.norbertotaveras.todo.room.entities.TodoEntity
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {
    val getAll: LiveData<MutableList<TodoEntity>> = todoDao.getAll()
    //val low: LiveData<MutableList<TodoEntity>> = todoDao.low()
    //val medium: LiveData<MutableList<TodoEntity>> = todoDao.medium()
    //val high: LiveData<MutableList<TodoEntity>> = todoDao.high(false)
    //val title: LiveData<MutableList<TodoEntity>> = todoDao.title(hideCompleted = false)

    suspend fun insert(todo: TodoEntity) {
        todoDao.insert(todo)
    }

    suspend fun update(todo: TodoEntity) {
        todoDao.update(todo)
    }

    suspend fun delete(todo: TodoEntity) {
        todoDao.delete(todo)
    }

    suspend fun deleteAll() {
        todoDao.deleteAll()
    }

    suspend fun deleteCompletedTasks() {
        todoDao.deleteCompletedTasks()
    }

    fun search(query: String): LiveData<MutableList<TodoEntity>> {
        return todoDao.search(query)
    }
}