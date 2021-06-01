package com.norbertotaveras.todo.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.norbertotaveras.todo.R
import com.norbertotaveras.todo.databinding.TodoItemLayoutBinding
import com.norbertotaveras.todo.models.Priority
import com.norbertotaveras.todo.room.entities.TodoEntity
import com.norbertotaveras.todo.utils.TodoDiffUtils

class TodosAdapter(private val listener: OnTodoHolderEventsListener? = null)
    : RecyclerView.Adapter<TodosAdapter.TodosViewHolder>() {

    var todos = mutableListOf<TodoEntity>()

    interface OnTodoHolderEventsListener {
        fun onCompleteTodo(todo: TodoEntity)
        fun onCheckBoxClick(todo: TodoEntity, isChecked: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TodoItemLayoutBinding.inflate(layoutInflater, parent, false)
        return TodosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodosViewHolder, position: Int) {
        holder.bind(todos[position])
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    inner class TodosViewHolder(private val binding: TodoItemLayoutBinding): RecyclerView.ViewHolder(binding.root){

        init {
            binding.apply {
                root.setOnClickListener {
                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        val todo = todos[pos]
                        listener?.onCompleteTodo(todo)
                    }
                }

                checkBoxCompleted.setOnClickListener {
                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        val todo = todos[pos]
                        listener?.onCheckBoxClick(todo, checkBoxCompleted.isChecked)
                    }
                }
            }
        }

        fun bind(todo: TodoEntity) {
            binding.apply {
                todoEntity = todo
                title.text = todo.title
                title.paint.isStrikeThruText = todo.completed
                description.text = todo.description
                checkBoxCompleted.isChecked = todo.completed

                when (todo.priority) {
                    Priority.HIGH -> {indicator.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.red))}
                    Priority.MEDIUM -> {indicator.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.yellow))}
                    Priority.LOW -> {indicator.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.green))}
                }

                if (todo.completed) {
                    styleAsDone()
                }

                binding.executePendingBindings()
            }
        }

        private fun styleAsDone() {
            binding.apply {
                /*circle.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        root.context.resources,
                        R.drawable.ic_baseline_radio_button_checked_24, null))*/
                title.paintFlags = title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }

        private fun removeDoneStyle() {
            binding.apply {
                /*circle.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        root.context.resources,
                        R.drawable.ic_round_radio_button_unchecked_24, null))*/
                title.paintFlags = 0
            }
        }

        /*companion object {
            fun from(parent: ViewGroup): TodosViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TodoItemLayoutBinding.inflate(layoutInflater, parent, false)
                return TodosViewHolder(binding)
            }
        }*/
    }

    fun set(todos: MutableList<TodoEntity>) {
        val diff = TodoDiffUtils(this.todos, todos)
        val result = DiffUtil.calculateDiff(diff)
        this.todos = todos
        result.dispatchUpdatesTo(this)
        //notifyDataSetChanged()
    }

    fun remove(pos: Int) {
        todos.removeAt(pos)
        notifyItemRemoved(pos)
    }
}


