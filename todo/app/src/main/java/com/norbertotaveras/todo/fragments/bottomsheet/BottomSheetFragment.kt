package com.norbertotaveras.todo.fragments.bottomsheet

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.norbertotaveras.todo.R
import com.norbertotaveras.todo.databinding.FragmentTaskBottomSheetBinding
import com.norbertotaveras.todo.utils.SortOrder
import com.norbertotaveras.todo.viewmodels.SharedViewModel
import com.norbertotaveras.todo.viewmodels.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class BottomSheetFragment : BottomSheetDialogFragment(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {

    private lateinit var groupSortAt: List<View>

    private var _binding: FragmentTaskBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val todoViewModel: TodoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBottomSheetBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
        binding.todoViewModel = todoViewModel

        groupSortAt = listOf(binding.imageSortingInput, binding.scrollViewSortAt)

        binding.highChoice.setOnClickListener(this)
        binding.mediumChoice.setOnClickListener(this)
        binding.lowChoice.setOnClickListener(this)
        binding.titleChoice.setOnClickListener(this)
        binding.date.setOnClickListener(this)
        binding.showCompletedSwitch.setOnCheckedChangeListener(this)
        binding.buttonDiscardBottomSheet.setOnClickListener(this)

        viewLifecycleOwner.lifecycleScope.launch {
            todoViewModel.preferencesFlow.collect { x ->
                when (x.sortOrder.ordinal) {
                    SortOrder.BY_HIGH.ordinal -> binding.chipGroupSortAt.check(R.id.high_choice)
                    SortOrder.BY_MEDIUM.ordinal -> binding.chipGroupSortAt.check(R.id.medium_choice)
                    SortOrder.BY_LOW.ordinal -> binding.chipGroupSortAt.check(R.id.low_choice)
                    SortOrder.BY_NAME.ordinal -> binding.chipGroupSortAt.check(R.id.title_choice)
                    SortOrder.BY_DATE.ordinal -> binding.chipGroupSortAt.check(R.id.date)
                }
                binding.showCompletedSwitch.isChecked = x.hideCompleted
            }
        }
        return binding.root
    }

    override fun onClick(v: View?) {
        val requestKey = "requestKey"
        var resultKey = ""
        var selected = 0
        var type = ""

        when (v?.id) {
            R.id.high_choice -> {
                selected = R.id.high_choice
                resultKey = "high_result"
                type = SortOrder.BY_HIGH.name }
            R.id.medium_choice-> {
                selected = R.id.medium_choice
                resultKey = "medium_result"
                type = SortOrder.BY_MEDIUM.name}
            R.id.low_choice -> {
                selected = R.id.low_choice
                resultKey = "low_result"
                type = SortOrder.BY_LOW.name }
            R.id.title_choice -> {
                selected = R.id.title_choice
                resultKey = "title_result"
                type = SortOrder.BY_NAME.name }
            R.id.date -> {
                selected = R.id.date
                resultKey = "date_result"
                type = SortOrder.BY_DATE.name
            }
            R.id.button_discard_bottom_sheet-> {
                this.dismiss()
            }
        }
        binding.chipGroupSortAt.check(selected)
        parentFragmentManager.setFragmentResult(
            requestKey,
            bundleOf(resultKey to type))
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.show_completed_switch -> {
                parentFragmentManager.setFragmentResult(
                    "completed_request",
                    bundleOf("completed_result" to binding.showCompletedSwitch.isChecked)
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        reset()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        reset()
    }

    private fun reset() {
        for (view in groupSortAt) {
            view.visibility = View.VISIBLE
        }
        binding.showCompletedSwitch.visibility = View.VISIBLE
        binding.showCompleted.visibility = View.VISIBLE
    }
}