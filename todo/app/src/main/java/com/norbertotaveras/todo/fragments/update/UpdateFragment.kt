package com.norbertotaveras.todo.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.norbertotaveras.todo.R
import com.norbertotaveras.todo.databinding.FragmentUpdateBinding
import com.norbertotaveras.todo.room.entities.TodoEntity
import com.norbertotaveras.todo.viewmodels.SharedViewModel
import com.norbertotaveras.todo.viewmodels.TodoViewModel

class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()
    private val sharedViewModel: SharedViewModel by viewModels()
    private val todoViewModel: TodoViewModel by viewModels()

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val view = inflater.inflate(R.layout.fragment_update, container, false)

        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        binding.args = args
        binding.currentPrioritySpinner.onItemSelectedListener = sharedViewModel.listener

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> update()
            R.id.menu_delete -> delete()
            else -> Log.e("Error", "Unexpected Menu Case: " + item.itemId.toString())
        }
        return super.onOptionsItemSelected(item)
    }

    private fun update() {
        val title = binding.currentTitleEt.text.toString()
        val description = binding.currentDescriptionEt.text.toString()
        val priority = binding.currentPrioritySpinner.selectedItem.toString()

        val validation = sharedViewModel.verify(title, description)
        if (validation) {
            val todo = TodoEntity(args.currentTodo.id, title, sharedViewModel.lookup(priority), description)
            todoViewModel.update(todo)
            Toast.makeText(requireContext(),getString(R.string.successfully_updated), Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(),getString(R.string.required_fields), Toast.LENGTH_SHORT).show()
        }
    }

    private fun delete() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(getString(R.string.yes)) {_,_ ->
            todoViewModel.delete(args.currentTodo)
            Toast.makeText(requireContext(), resources.getString(R.string.successfully_removed, args.currentTodo.title), Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No") {_,_ -> }
        builder.setTitle(resources.getString(R.string.delete_prompt, args.currentTodo.title))
        builder.setMessage(resources.getString(R.string.confirm_prompt, args.currentTodo.title))
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
