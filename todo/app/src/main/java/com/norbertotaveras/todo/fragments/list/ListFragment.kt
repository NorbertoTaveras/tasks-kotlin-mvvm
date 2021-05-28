package com.norbertotaveras.todo.fragments.list

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.norbertotaveras.todo.R
import com.norbertotaveras.todo.adapters.TodosAdapter
import com.norbertotaveras.todo.viewmodels.TodoViewModel

class ListFragment : Fragment(), View.OnClickListener {

    private lateinit var button: FloatingActionButton
    private lateinit var layout: ConstraintLayout
    private lateinit var recyclerview: RecyclerView

    private val adapter: TodosAdapter by lazy { TodosAdapter() }
    private val todoViewModel: TodoViewModel by viewModels()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        button = view.findViewById(R.id.floatingActionButton)
        layout = view.findViewById(R.id.listLayout)
        recyclerview = view.findViewById(R.id.recyclerView)

        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(requireActivity())

        todoViewModel.todos.observe(viewLifecycleOwner, Observer { todos ->
            adapter.set(todos)
        })

        button.setOnClickListener(this)
        layout.setOnClickListener(this)

        setHasOptionsMenu(true)
        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.floatingActionButton -> findNavController().navigate(R.id.action_listFragment_to_addFragment)
            else -> Log.e("Error", "Unexpected Case")
        }
    }
}