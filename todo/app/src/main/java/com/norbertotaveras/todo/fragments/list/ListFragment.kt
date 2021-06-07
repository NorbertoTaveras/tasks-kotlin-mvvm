package com.norbertotaveras.todo.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.norbertotaveras.todo.R
import com.norbertotaveras.todo.adapters.TodosAdapter
import com.norbertotaveras.todo.databinding.FragmentListBinding
import com.norbertotaveras.todo.fragments.bottomsheet.BottomSheetFragment
import com.norbertotaveras.todo.room.entities.TodoEntity
import com.norbertotaveras.todo.utils.SortOrder
import com.norbertotaveras.todo.utils.createSnackBar
import com.norbertotaveras.todo.utils.hideKeyboard
import com.norbertotaveras.todo.utils.observeOnce
import com.norbertotaveras.todo.viewmodels.SharedViewModel
import com.norbertotaveras.todo.viewmodels.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

const val SPAN_COUNT_ONE = 1
const val SPAN_COUNT_TWO = 2

@AndroidEntryPoint
class ListFragment : Fragment(), SearchView.OnQueryTextListener, TodosAdapter.OnTodoHolderEventsListener,
    View.OnClickListener {

    private lateinit var button: FloatingActionButton
    private lateinit var recyclerview: RecyclerView
    private lateinit var warningImage: ImageView
    private lateinit var warningText: TextView
    private lateinit var searchMenuItem: MenuItem
    private lateinit var searchView: SearchView
    private lateinit var layoutType: StaggeredGridLayoutManager

    private val adapter: TodosAdapter by lazy { TodosAdapter(this) }
    private val todoViewModel: TodoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim)}
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim)}
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim)}
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim)}

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private var isClicked: Boolean = false
    private var currentQuery: String? = null

    private lateinit var bottomSheetFragment: BottomSheetFragment

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
        searchMenuItem = menu.findItem(R.id.menu_search)
        searchView = searchMenuItem.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)

        val pendingQuery = currentQuery
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchMenuItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            /*menu.findItem(R.id.hide_completed).isChecked =
                todoViewModel.preferencesFlow.first().hideCompleted */
        }
    }

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
        binding.todoViewModel = todoViewModel

        todoViewModel.tasks.observe(viewLifecycleOwner, Observer { tasks ->
            sharedViewModel.check(tasks)
            adapter.set(tasks)
        })

        setup()
        setHasOptionsMenu(true)

        hideKeyboard(requireActivity())

        childFragmentManager.setFragmentResultListener(
            "requestKey", viewLifecycleOwner) {_, bundle ->
            when {
                bundle.getString("high_result") == SortOrder.BY_HIGH.name -> {
                    todoViewModel.onSortOrderSelected(SortOrder.BY_HIGH) }
                bundle.getString("medium_result") == SortOrder.BY_MEDIUM.name -> {
                    todoViewModel.onSortOrderSelected(SortOrder.BY_MEDIUM) }
                bundle.getString("low_result") == SortOrder.BY_LOW.name -> {
                    todoViewModel.onSortOrderSelected(SortOrder.BY_LOW) }
                bundle.getString("title_result") == SortOrder.BY_NAME.name -> {
                    todoViewModel.onSortOrderSelected(SortOrder.BY_NAME) }
                bundle.getString("date_result") == SortOrder.BY_DATE.name -> {
                    todoViewModel.onSortOrderSelected(SortOrder.BY_DATE) }
                else -> { }
            }
        }

        childFragmentManager.setFragmentResultListener(
            "completed_request", viewLifecycleOwner) {_, bundle ->
            val result = bundle.getBoolean("completed_result")
            todoViewModel.onHideCompletedClick(result)
        }

        setFragmentResultListener("delete_request") {_, bundle ->
            val result = bundle.getInt("delete_result")
            todoViewModel.onUpdateDeleteResult(result)
        }

        setFragmentResultListener("update_request") {_, bundle ->
            val result = bundle.getInt("update_result")
            todoViewModel.onUpdateDeleteResult(result)
        }

        setFragmentResultListener("add_request") {_, bundle ->
            val result = bundle.getInt("add_result")
            todoViewModel.onAddResult(result)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            todoViewModel.tasksEvent.collect { event ->
                when (event) {
                    is TodoViewModel.TasksEvent.ShowTaskDeleteMessage -> {
                        createSnackBar(requireView(), event.res, Snackbar.LENGTH_SHORT) }
                    is TodoViewModel.TasksEvent.ShowTaskUpdateMessage -> {
                        createSnackBar(requireView(), event.res, Snackbar.LENGTH_SHORT) }
                    is TodoViewModel.TasksEvent.ShowAddMessage -> {
                        createSnackBar(requireView(), event.res, Snackbar.LENGTH_SHORT) }
                }
            }
        }

        bottomSheetFragment = BottomSheetFragment()
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            /*R.id.menu_priority_low -> { todoViewModel.onSortOrderSelected(SortOrder.BY_LOW) }
            R.id.menu_priority_medium -> { todoViewModel.onSortOrderSelected(SortOrder.BY_MEDIUM) }
            R.id.menu_priority_high -> { todoViewModel.onSortOrderSelected(SortOrder.BY_HIGH) }
            R.id.sort_by_title -> { todoViewModel.onSortOrderSelected(SortOrder.BY_NAME) }
            R.id.sort_by_date_created -> { todoViewModel.onSortOrderSelected(SortOrder.BY_DATE) }
            R.id.hide_completed -> {
                item.isChecked = !item.isChecked
                todoViewModel.onHideCompletedClick(item.isChecked) } */
            //R.id.delete_all_completed -> {deleteCompletedTasks()}
            R.id.view_type -> {
                switchLayout()
                switchIcon(item)
            }
            else -> {super.onOptionsItemSelected(item)}

        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchView.setOnQueryTextListener(null)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null)
            search(query)
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        currentQuery = query
        if (query != null)
            search(query)
        return true
    }

    private fun deleteAll() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(getString(R.string.yes)) {_,_ ->
            todoViewModel.deleteAll()
            createSnackBar(requireView(), R.string.successfully_removed_all, Snackbar.LENGTH_LONG)
        }
        builder.setNegativeButton("No") {_,_ -> }
        builder.setTitle(getString(R.string.removed_all_todos_prompt))
        builder.setMessage(getString(R.string.remove_all_confirm_prompt))
        builder.create().show()
    }

    private fun setup() {
        button = binding.addFloatingButton
        warningImage = binding.warningImage
        warningText = binding.warningText

        binding.mainFloatingButton.setOnClickListener(this)
        binding.deleteAllFloatingButton.setOnClickListener(this)
        binding.filterFloatingButton.setOnClickListener(this)
        binding.deleteAllCompletedFloatingButton.setOnClickListener(this)

        recyclerview = binding.recyclerView
        recyclerview.adapter = adapter
        layoutType = StaggeredGridLayoutManager(SPAN_COUNT_TWO, StaggeredGridLayoutManager.VERTICAL)
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
                restore(viewHolder.itemView, item, viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(view)
    }

    private fun restore(view: View, todo: TodoEntity, pos: Int) {
        val snackBar = Snackbar.make(view, resources.getString(R.string.task_successfully_removed), Snackbar.LENGTH_LONG)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.main_floating_button -> {onMainButtonClick()}
            R.id.delete_all_floating_button -> {
                deleteAll()
                setVisibility(false)
                setAnimation(false) }
            R.id.filter_floating_button -> {
                bottomSheetFragment.show(childFragmentManager,bottomSheetFragment.tag)
                setVisibility(false)
                setAnimation(false) }
            R.id.delete_all_completed_floating_button -> {
                deleteCompletedTasks()
                setVisibility(false)
                setAnimation(false)
            }
        }
    }

    private fun onMainButtonClick() {
        setVisibility(isClicked)
        setAnimation(isClicked)
        isClicked = !isClicked
    }

    private fun setVisibility(isClicked: Boolean) {
        when (!isClicked) {
            false -> {
                binding.deleteAllFloatingButton.visibility = View.VISIBLE
                binding.addFloatingButton.visibility = View.VISIBLE
                binding.filterFloatingButton.visibility = View.VISIBLE
                binding.deleteAllCompletedFloatingButton.visibility = View.VISIBLE
            }
            true -> {
                binding.deleteAllFloatingButton.visibility = View.GONE
                binding.addFloatingButton.visibility = View.GONE
                binding.filterFloatingButton.visibility = View.GONE
                binding.deleteAllCompletedFloatingButton.visibility = View.GONE
            }
        }
    }

    private fun setAnimation(isClicked: Boolean) {
        when (!isClicked) {
            false -> {
                binding.deleteAllCompletedFloatingButton.startAnimation(fromBottom)
                binding.deleteAllFloatingButton.startAnimation(fromBottom)
                binding.addFloatingButton.startAnimation(fromBottom)
                binding.filterFloatingButton.startAnimation(fromBottom)
                binding.mainFloatingButton.startAnimation(rotateOpen)
            }
            true -> {
                binding.deleteAllCompletedFloatingButton.startAnimation(toBottom)
                binding.deleteAllFloatingButton.startAnimation(toBottom)
                binding.addFloatingButton.startAnimation(toBottom)
                binding.filterFloatingButton.startAnimation(toBottom)
                binding.mainFloatingButton.startAnimation(rotateClose)
            }
        }
    }

    private fun switchLayout() {
        when (layoutType.spanCount) {
            SPAN_COUNT_TWO -> { layoutType.spanCount = SPAN_COUNT_ONE }
            SPAN_COUNT_ONE -> { layoutType.spanCount = SPAN_COUNT_TWO }
        }
        adapter.notifyItemChanged(0, adapter.itemCount)
    }

    private fun switchIcon(item: MenuItem) {
        when (layoutType.spanCount) {
            SPAN_COUNT_TWO -> item.setIcon(R.drawable.ic_round_grid_view_24)
            SPAN_COUNT_ONE -> item.setIcon(R.drawable.ic_round_view_list_24)
        }
    }

    private fun deleteCompletedTasks() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(getString(R.string.yes)) {_,_ ->
            todoViewModel.deleteCompletedTasks()
        }
        builder.setNegativeButton(getString(R.string.no)) { _, _ -> }
        builder.setTitle(getString(R.string.confirm_deletion_prompt))
        builder.setMessage(getString(R.string.confirm_delete_all_completed_tasks))
        builder.create().show()
    }
}
