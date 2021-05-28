package com.norbertotaveras.todo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.norbertotaveras.todo.R
import com.norbertotaveras.todo.fragments.list.ListFragmentDirections
import com.norbertotaveras.todo.models.Priority
import com.norbertotaveras.todo.room.entities.TodoEntity

class TodosAdapter: RecyclerView.Adapter<TodosAdapter.TodosViewHolder>() {

    var todos = emptyList<TodoEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item_layout, parent,false)
        return TodosViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodosViewHolder, position: Int) {
        holder.bind(todos[position])
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    class TodosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val title: TextView = itemView.findViewById(R.id.title)
        private val description: TextView = itemView.findViewById(R.id.description)
        private val indicator: MaterialCardView = itemView.findViewById(R.id.indicator)

        var todo: TodoEntity? = null
        fun bind(todo: TodoEntity) {
            this.todo = todo
            title.text = todo.title
            description.text = todo.description

            when (todo.priority) {
                Priority.HIGH -> {indicator.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.red))}
                Priority.MEDIUM -> {indicator.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.yellow))}
                Priority.LOW -> {indicator.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.green))}
            }

            itemView.setOnClickListener {
                val action = ListFragmentDirections.actionListFragmentToUpdateFragment(todo)
                itemView.findNavController().navigate(action)
            }
        }
    }

    fun set(todos: List<TodoEntity>) {
        this.todos = todos
        notifyDataSetChanged()
    }
}