package com.norbertotaveras.todo.utils

import androidx.recyclerview.widget.DiffUtil
import com.norbertotaveras.todo.room.entities.TodoEntity

class TodoDiffUtils(private val old: MutableList<TodoEntity>,
                    private val new: MutableList<TodoEntity>) : DiffUtil.Callback(){

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] === new[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].id == new[newItemPosition].id
                && old[oldItemPosition].title == new[newItemPosition].title
                && old[oldItemPosition].description == new[newItemPosition].description
                && old[oldItemPosition].priority == new[newItemPosition].priority
    }
}