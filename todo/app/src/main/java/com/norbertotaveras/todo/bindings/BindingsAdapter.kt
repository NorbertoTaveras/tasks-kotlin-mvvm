package com.norbertotaveras.todo.bindings

import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Spinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.norbertotaveras.todo.R
import com.norbertotaveras.todo.fragments.list.ListFragmentDirections
import com.norbertotaveras.todo.models.Priority
import com.norbertotaveras.todo.room.entities.TodoEntity
import com.tiper.MaterialSpinner

class BindingsAdapter {
    companion object {

        @BindingAdapter("android:navigateToAddFragment")
        @JvmStatic
        fun navigateToAddFragment(view: FloatingActionButton, navigate: Boolean) {
            view.setOnClickListener {
                when (navigate) {
                    true -> view.findNavController().navigate(R.id.action_listFragment_to_addFragment)
                    false -> Log.e("Navigate", "Unexpected Navigation")
                }
            }
        }

        @BindingAdapter("android:emptyViews")
        @JvmStatic
        fun emptyViews(view: View, empty: MutableLiveData<Boolean>) {
            when (empty.value) {
                true -> view.visibility = View.VISIBLE
                false -> view.visibility = View.INVISIBLE
            }
        }

        @BindingAdapter("android:parsePriority")
        @JvmStatic
        fun parsePriority(view: MaterialSpinner, priority: Priority) {
            when (priority) {
                Priority.HIGH -> {view.selection = 0}
                Priority.MEDIUM -> {view.selection = 1}
                Priority.LOW -> {view.selection = 2}
            }
        }

        @BindingAdapter("android:parsePriorityColor")
        @JvmStatic
        fun parsePriorityColor(cardView: MaterialCardView, priority: Priority) {
            when (priority) {
                Priority.HIGH -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cardView.setCardBackgroundColor(cardView.context.getColor(R.color.red))
                    } else {
                        cardView.setCardBackgroundColor(ContextCompat.getColor(cardView.context, R.color.red))
                    }
                }
                Priority.MEDIUM -> {cardView.setCardBackgroundColor(ContextCompat.getColor(cardView.context, R.color.yellow))}
                Priority.LOW -> {cardView.setCardBackgroundColor(ContextCompat.getColor(cardView.context, R.color.green))}
            }
        }

        @BindingAdapter("android:sendDataToUpdateFragment")
        @JvmStatic
        fun sendDataToUpdateFragment(view: ConstraintLayout, current: TodoEntity) {
            view.setOnClickListener {
                val action = ListFragmentDirections.actionListFragmentToUpdateFragment(current)
                view.findNavController().navigate(action)
            }
        }
    }
}