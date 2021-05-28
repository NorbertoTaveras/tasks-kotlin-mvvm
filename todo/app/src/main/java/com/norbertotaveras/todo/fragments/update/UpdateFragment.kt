package com.norbertotaveras.todo.fragments.update

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.norbertotaveras.todo.R
import com.norbertotaveras.todo.models.Priority
import com.norbertotaveras.todo.viewmodels.SharedViewModel

class UpdateFragment : Fragment() {

    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var spinner: Spinner

    private val args by navArgs<UpdateFragmentArgs>()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update, container, false)

        title = view.findViewById(R.id.current_title_et)
        description = view.findViewById(R.id.current_description_et)
        spinner = view.findViewById(R.id.current_priority_spinner)

        title.setText(args.currentTodo.title)
        description.setText(args.currentTodo.description)
        spinner.setSelection(parse(args.currentTodo.priority))
        spinner.onItemSelectedListener = sharedViewModel.listener
        setHasOptionsMenu(true)
        return view
    }

    private fun parse(priority: Priority): Int {
        return when (priority) {
            Priority.HIGH -> 0
            Priority.MEDIUM -> 1
            Priority.LOW -> 2
        }
    }
}