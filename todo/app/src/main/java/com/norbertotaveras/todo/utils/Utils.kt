package com.norbertotaveras.todo.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.norbertotaveras.todo.R
import com.norbertotaveras.todo.fragments.update.UpdateFragmentArgs
import com.norbertotaveras.todo.room.entities.TodoEntity

fun hideKeyboard(activity: Activity) {
    val manager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val focusedView = activity.currentFocus
    focusedView?.let {
        manager.hideSoftInputFromWindow(focusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun createSnackBar(view: View, @StringRes message: Int, duration: Int) {
    val resources = view.resources
    val snackBar = Snackbar.make(view, resources.getString(message), duration)
    snackBar.show()
}

fun createSnackBar(view: View, @StringRes message: Int, args: Any, duration: Int) {
    val resources = view.resources
    val snackBar = Snackbar.make(view, resources.getString(message, args), duration)
    snackBar.show()
}

fun createSnackBar(view: View,  message: String, duration: Int) {
    val snackBar = Snackbar.make(view, message, duration)
    snackBar.show()
}