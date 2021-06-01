package com.norbertotaveras.todo.viewmodels

import android.app.Application
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.norbertotaveras.todo.R
import com.norbertotaveras.todo.models.Priority
import com.norbertotaveras.todo.room.entities.TodoEntity

class SharedViewModel(application: Application): AndroidViewModel(application) {

    val empty: MutableLiveData<Boolean> = MutableLiveData(false)

    val listener: AdapterView.OnItemSelectedListener = object :
        AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long) {
            when (position) {
                0 -> {(parent?.getChildAt(0) as? TextView)?.setTextColor(ContextCompat.getColor(application, R.color.red))}
                1 -> {(parent?.getChildAt(0) as? TextView)?.setTextColor(ContextCompat.getColor(application, R.color.yellow))}
                2 -> {(parent?.getChildAt(0) as? TextView)?.setTextColor(ContextCompat.getColor(application, R.color.green))}
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    fun verify(title: String, description: String): Boolean {
        return !(title.isEmpty() || description.isEmpty())
    }

    fun lookup(priority: String): Priority {
        return when (priority) {
            "High" -> Priority.HIGH
            "Medium" -> Priority.MEDIUM
            "Low" -> Priority.LOW
            else -> Priority.LOW
        }
    }

    fun parse(priority: Priority): Int {
        return when (priority) {
            Priority.HIGH -> 0
            Priority.MEDIUM -> 1
            Priority.LOW -> 2
        }
    }

    fun check(todos: List<TodoEntity>) {
        empty.value = todos.isEmpty()
    }
}