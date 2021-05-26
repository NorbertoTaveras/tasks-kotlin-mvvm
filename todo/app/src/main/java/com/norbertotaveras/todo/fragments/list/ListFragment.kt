package com.norbertotaveras.todo.fragments.list

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.norbertotaveras.todo.R

class ListFragment : Fragment(), View.OnClickListener {

    private lateinit var button: FloatingActionButton
    private lateinit var layout: ConstraintLayout

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

        button.setOnClickListener(this)
        layout.setOnClickListener(this)

        setHasOptionsMenu(true)
        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.floatingActionButton -> findNavController().navigate(R.id.action_listFragment_to_addFragment)
            R.id.listLayout -> findNavController().navigate(R.id.action_listFragment_to_updateFragment)
            else -> Log.e("Error", "Unexpected Case")
        }
    }
}