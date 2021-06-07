package com.norbertotaveras.todo.viewmodels

import android.app.Application
import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.todo.*
import com.norbertotaveras.todo.repositories.TodoRepository
import com.norbertotaveras.todo.room.database.TodoDatabase
import com.norbertotaveras.todo.room.entities.TodoEntity
import com.norbertotaveras.todo.utils.PreferencesManager
import com.norbertotaveras.todo.utils.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(application: Application, private val preferencesManager: PreferencesManager): AndroidViewModel(application) {

    private val todoDao = TodoDatabase.getInstance(application).todoDao()
    private val repository: TodoRepository

    private val updateDeleteTaskEventChannel = Channel<UpdateDeleteTaskEvent>()
    private val tasksEventChannel = Channel<TasksEvent>()
    private val addTaskEventChannel = Channel<AddTaskEvent>()

    val tasksEvent = tasksEventChannel.receiveAsFlow()
    val updateDeleteTaskEvent = updateDeleteTaskEventChannel.receiveAsFlow()
    val addTaskEvent = addTaskEventChannel.receiveAsFlow()
    val preferencesFlow = preferencesManager.preferencesFlow

    @ExperimentalCoroutinesApi
    private val tasksFlow = combine(
        preferencesFlow
    ) { filterPreferences ->
        filterPreferences
    }.flatMapLatest { (filterPreferences) ->
        todoDao.getAllFlow(filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    @ExperimentalCoroutinesApi
    val tasks = tasksFlow.asLiveData()

    init {
        repository = TodoRepository(todoDao)
    }

    fun insert(todo: TodoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(todo)
            addTaskEventChannel.send(AddTaskEvent.NavigateBackWithAddResult(ADD_TASK_RESULT_OK))
        }
    }

    fun update(todo: TodoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(todo)
            updateDeleteTaskEventChannel.send(
                UpdateDeleteTaskEvent.NavigateBackWithUpdateResult(UPDATE_TASK_RESULT_OK))
        }
    }

    fun delete(todo: TodoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(todo)
            updateDeleteTaskEventChannel.send(
                UpdateDeleteTaskEvent.NavigateBackWithDeleteResult(DELETE_TASK_RESULT_OK))
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    fun deleteCompletedTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCompletedTasks()
        }
    }

    fun search(query: String): LiveData<MutableList<TodoEntity>> {
        return repository.search(query)
    }

    fun onTaskSelected(todo: TodoEntity) {
    }

    fun onSortOrderSelected(sortOrder: SortOrder) {
        viewModelScope.launch {
            preferencesManager.updateSortOrder(sortOrder)
        }
    }

    fun onHideCompletedClick(hideCompleted: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateHideCompleted(hideCompleted)
        }
    }

    fun onTaskCheckedChanged(todo: TodoEntity, isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(todo.copy(completed = isChecked))
        }
    }

    fun onUpdateDeleteResult(result: Int) {
        when (result) {
            DELETE_TASK_RESULT_OK -> showDeleteMessage(R.string.task_successfully_removed)
            UPDATE_TASK_RESULT_OK -> showUpdateMessage(R.string.successfully_updated)
        }
    }

    fun onAddResult(result: Int) {
        when(result) {
            ADD_TASK_RESULT_OK -> showAddMessage(R.string.successfully_added)
        }
    }

    private fun showDeleteMessage(res: Int) {
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.ShowTaskDeleteMessage(res))
        }
    }

    private fun showUpdateMessage(res: Int) {
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.ShowTaskUpdateMessage(res))
        }
    }

    private fun showAddMessage(res: Int) {
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.ShowAddMessage(res))
        }
    }

    sealed class AddTaskEvent {
        data class NavigateBackWithAddResult(val result: Int): AddTaskEvent()
    }

    sealed class UpdateDeleteTaskEvent {
        data class NavigateBackWithDeleteResult(val result: Int) : UpdateDeleteTaskEvent()
        data class NavigateBackWithUpdateResult(val result: Int) : UpdateDeleteTaskEvent()
    }

    sealed class TasksEvent {
        data class ShowTaskDeleteMessage(@StringRes val res: Int): TasksEvent()
        data class ShowTaskUpdateMessage(@StringRes val res: Int): TasksEvent()
        data class ShowAddMessage(@StringRes val res: Int): TasksEvent()
    }
}

