package com.norbertotaveras.todo.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.todo.models.Todo
import com.norbertotaveras.todo.repositories.TodoRepository
import com.norbertotaveras.todo.room.database.TodoDatabase
import com.norbertotaveras.todo.room.entities.TodoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel(application: Application): AndroidViewModel(application) {

    private val todoDao = TodoDatabase.getInstance(application).todoDao()
    private val repository: TodoRepository
    val todos: LiveData<MutableList<TodoEntity>>
    val low: LiveData<MutableList<TodoEntity>>
    val medium: LiveData<MutableList<TodoEntity>>
    val high: LiveData<MutableList<TodoEntity>>

    init {
        repository = TodoRepository(todoDao)
        todos = repository.getAll
        low = repository.low
        medium = repository.medium
        high = repository.high
    }

    fun insert(todo: TodoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(todo)
        }
    }

    fun update(todo: TodoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(todo)
        }
    }

    fun delete(todo: TodoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(todo)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    fun search(query: String): LiveData<MutableList<TodoEntity>> {
        return repository.search(query)
    }

    fun completed(todo: TodoEntity, complete: Boolean) {
        todo.completed = complete
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(todo)
        }
    }

    fun onTaskSelected(todo: TodoEntity) {
    }

    fun onTaskCheckedChanged(todo: TodoEntity, isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(todo.copy(completed = isChecked))
        }
    }

}