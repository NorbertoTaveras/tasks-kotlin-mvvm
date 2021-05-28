package com.norbertotaveras.todo.fragments.add

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.norbertotaveras.todo.R
import com.norbertotaveras.todo.models.Priority
import com.norbertotaveras.todo.room.entities.TodoEntity
import com.norbertotaveras.todo.viewmodels.SharedViewModel
import com.norbertotaveras.todo.viewmodels.TodoViewModel

class AddFragment : Fragment() {

    private lateinit var title: EditText
    private lateinit var priorities: Spinner
    private lateinit var description: EditText

    private val todoViewModel: TodoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        setHasOptionsMenu(true)

        title = view.findViewById(R.id.title_et)
        description = view.findViewById(R.id.description_et)
        priorities = view.findViewById(R.id.priority_spinner)
        priorities.onItemSelectedListener = sharedViewModel.listener
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> insertTodo()
            else -> Log.e("Menu", "Unexpected Menu Item")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertTodo() {
        val title = title.text.toString()
        val priority = priorities.selectedItem.toString()
        val description = description.text.toString()

        val validation = sharedViewModel.verify(title, description)

        if (validation) {
            val todo = TodoEntity(0, title, sharedViewModel.lookup(priority), description)
            todoViewModel.insert(todo)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
        }
    }


}