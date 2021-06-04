package com.norbertotaveras.todo.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.norbertotaveras.todo.R
import com.norbertotaveras.todo.databinding.FragmentUpdateBinding
import com.norbertotaveras.todo.room.entities.TodoEntity
import com.norbertotaveras.todo.utils.createSnackBar
import com.norbertotaveras.todo.utils.hideKeyboard
import com.norbertotaveras.todo.viewmodels.SharedViewModel
import com.norbertotaveras.todo.viewmodels.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
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
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        binding.args = args

        val adapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.priorities,
            android.R.layout.simple_spinner_dropdown_item)

        binding.currentPrioritySpinner.adapter = adapter
        binding.currentPrioritySpinner.onItemSelectedListener = sharedViewModel.tiper
        binding.dateCreated.text = resources.getString(R.string.date_created_value, args.currentTodo.createdDateFormatted)
        setHasOptionsMenu(true)
        hideKeyboard(requireActivity())
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            todoViewModel.updateDeleteTaskEvent.collect { event ->
                when (event) {
                    is TodoViewModel.UpdateDeleteTaskEvent.NavigateBackWithDeleteResult -> {
                        setFragmentResult("delete_request", bundleOf("delete_result" to event.result))
                        findNavController().navigate(R.id.action_updateFragment_to_listFragment)
                    }
                    is TodoViewModel.UpdateDeleteTaskEvent.NavigateBackWithUpdateResult -> {
                        setFragmentResult("update_request", bundleOf("update_result" to event.result))
                        findNavController().navigate(R.id.action_updateFragment_to_listFragment)
                    }
                }
            }
        }
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

        when (title.isEmpty()) {
            true -> {binding.currentTitleEtLayout.error = getString(R.string.error_title_field_required)}
            false -> {binding.currentTitleEtLayout.error = null}
        }

        when (description.isEmpty()) {
            true -> {binding.currentDescriptionEtLayout.error = getString(R.string.error_description_field_required)}
            false -> {binding.currentDescriptionEtLayout.error = null}
        }

        if (validation) {
            val todo = TodoEntity(args.currentTodo.id, title, sharedViewModel.lookup(priority), description)
            todoViewModel.update(todo)
            hideKeyboard(requireActivity())
        } else {
            createSnackBar(requireView(), R.string.required_fields, Snackbar.LENGTH_SHORT)
        }
    }

    private fun delete() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(getString(R.string.yes)) {_,_ ->
            todoViewModel.delete(args.currentTodo)
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
