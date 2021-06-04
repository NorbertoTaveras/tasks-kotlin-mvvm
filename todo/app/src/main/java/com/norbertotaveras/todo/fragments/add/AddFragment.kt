package com.norbertotaveras.todo.fragments.add

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.norbertotaveras.todo.R
import com.norbertotaveras.todo.databinding.FragmentAddBinding
import com.norbertotaveras.todo.models.Priority
import com.norbertotaveras.todo.room.entities.TodoEntity
import com.norbertotaveras.todo.utils.createSnackBar
import com.norbertotaveras.todo.utils.hideKeyboard
import com.norbertotaveras.todo.viewmodels.SharedViewModel
import com.norbertotaveras.todo.viewmodels.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddFragment : Fragment() {

    private val todoViewModel: TodoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)

        val adapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.priorities,
            android.R.layout.simple_spinner_dropdown_item)

        binding.prioritySpinner.adapter = adapter
        binding.prioritySpinner.onItemSelectedListener = sharedViewModel.tiper

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            todoViewModel.addTaskEvent.collect { event ->
                when (event) {
                    is TodoViewModel.AddTaskEvent.NavigateBackWithAddResult -> {
                        setFragmentResult("add_request", bundleOf("add_result" to event.result))
                        findNavController().navigate(R.id.action_addFragment_to_listFragment)
                    }
                }
            }
        }

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> insertTodo()
            else -> Log.e("Menu", "Unexpected Menu Item")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertTodo() {
        val title = binding.titleEt.text.toString()
        val priority = binding.prioritySpinner.selectedItem.toString()
        val description = binding.descriptionEt.text.toString()
        val validation = sharedViewModel.verify(title, description)

        when (title.isEmpty()) {
            true -> {binding.titleEtLayout.error = getString(R.string.error_title_field_required)}
            false -> {binding.titleEtLayout.error = null}
        }

        when (description.isEmpty()) {
            true -> {binding.descriptionEtLayout.error = getString(R.string.error_description_field_required)}
            false -> {binding.descriptionEtLayout.error = null}
        }

        if (validation) {
            val todo = TodoEntity(0, title, sharedViewModel.lookup(priority), description, false)
            todoViewModel.insert(todo)
            hideKeyboard(requireActivity())
        } else {
            createSnackBar(requireView(), R.string.required_fields, Snackbar.LENGTH_LONG)
        }
    }
}