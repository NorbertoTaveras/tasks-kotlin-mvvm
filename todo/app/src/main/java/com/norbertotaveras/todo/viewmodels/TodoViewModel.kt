package com.norbertotaveras.todo.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.todo.repositories.TodoRepository
import com.norbertotaveras.todo.room.database.TodoDatabase
import com.norbertotaveras.todo.room.entities.TodoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel(application: Application): AndroidViewModel(application) {

    private val todoDao = TodoDatabase.getInstance(application).todoDao()
    private val repository: TodoRepository
    val todos: LiveData<List<TodoEntity>>

    init {
        repository = TodoRepository(todoDao)
        todos = repository.getAll
    }

    fun insert(todo: TodoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(todo)
        }
    }
}