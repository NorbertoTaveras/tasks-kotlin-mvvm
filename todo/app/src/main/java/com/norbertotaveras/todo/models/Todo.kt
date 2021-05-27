package com.norbertotaveras.todo.models

data class Todo(
    var id: Int,
    var title: String,
    var priority: Priority,
    var description: String){
}