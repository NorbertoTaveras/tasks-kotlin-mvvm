package com.norbertotaveras.todo.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.norbertotaveras.todo.R
import com.norbertotaveras.todo.adapters.TodosAdapter
import com.norbertotaveras.todo.databinding.FragmentListBinding
import com.norbertotaveras.todo.room.entities.TodoEntity
import com.norbertotaveras.todo.utils.hideKeyboard
import com.norbertotaveras.todo.utils.observeOnce
import com.norbertotaveras.todo.viewmodels.SharedViewModel
import com.norbertotaveras.todo.viewmodels.TodoViewModel
import jp.wasabeef.recyclerview.animators.LandingAnimator

class ListFragment : Fragment(), SearchView.OnQueryTextListener, TodosAdapter.OnTodoHolderEventsListener {

    private lateinit var button: FloatingActionButton
    private lateinit var recyclerview: RecyclerView
    private lateinit var warningImage: ImageView
    private lateinit var warningText: TextView
    private lateinit var searchMenuItem: MenuItem
    private lateinit var searchView: SearchView
    private lateinit var layoutType: RecyclerView.LayoutManager

    private val adapter: TodosAdapter by lazy { TodosAdapter(this) }
    private val todoViewModel: TodoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
        searchMenuItem = menu.findItem(R.id.menu_search)
        searchView = searchMenuItem.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
        binding.todoViewModel = todoViewModel


        todoViewModel.todos.observe(viewLifecycleOwner, Observer { todos ->
            sharedViewModel.check(todos)
            //testAdapter.set(todos)
            adapter.set(todos)
        })
        setup()
        setHasOptionsMenu(true)

        hideKeyboard(requireActivity())
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete_all -> deleteAll()
            R.id.menu_priority_low -> todoViewModel.low.observe(viewLifecycleOwner, Observer {adapter.set(it)})
            R.id.menu_priority_medium -> todoViewModel.medium.observe(viewLifecycleOwner, Observer {adapter.set(it)})
            R.id.menu_priority_high -> todoViewModel.high.observe(viewLifecycleOwner, Observer {adapter.set(it)})
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null)
            search(query)
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null)
            search(query)
        return true
    }

    private fun deleteAll() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(getString(R.string.yes)) {_,_ ->
            todoViewModel.deleteAll()
            Toast.makeText(requireContext(), R.string.successfully_removed_all, Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No") {_,_ -> }
        builder.setTitle(getString(R.string.removed_all_todos_prompt))
        builder.setMessage(getString(R.string.remove_all_confirm_prompt))
        builder.create().show()
    }

    private fun setup() {
        button = binding.floatingActionButton
        warningImage = binding.warningImage
        warningText = binding.warningText

        ///testAdapter = TodosAdapter(this)

        recyclerview = binding.recyclerView
        recyclerview.adapter = adapter
        layoutType = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerview.layoutManager = layoutType
        recyclerview.itemAnimator = LandingAnimator().apply {
            addDuration = 300
        }

        swipe(recyclerview)
    }

    private fun swipe(view: RecyclerView) {
        val callback = object : SwipeToDelete(requireContext(), layoutType) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = adapter.todos[viewHolder.adapterPosition]
                todoViewModel.delete(item)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                //Toast.makeText(requireContext(), resources.getString(R.string.successfully_removed, item.title), Toast.LENGTH_SHORT).show()
                restore(viewHolder.itemView, item, viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(view)
    }

    private fun restore(view: View, todo: TodoEntity, pos: Int) {
        val snackBar = Snackbar.make(view, resources.getString(R.string.deleted, todo.title), Snackbar.LENGTH_LONG)
        snackBar.setAction(getString(R.string.undo)) {
            todoViewModel.insert(todo)
        }
        snackBar.show()
    }

    private fun search(query: String) {
        val searchedQuery = "%$query%"
        todoViewModel.search(searchedQuery).observeOnce(viewLifecycleOwner, Observer { list ->
            list?.let { adapter.set(it) }
        })
    }

    override fun onCompleteTodo(todo: TodoEntity) {
        todoViewModel.onTaskSelected(todo)
    }

    override fun onCheckBoxClick(todo: TodoEntity, isChecked: Boolean) {
        todoViewModel.onTaskCheckedChanged(todo, isChecked)
    }

}
