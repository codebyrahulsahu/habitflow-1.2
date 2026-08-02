package com.habitflow.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Small helper so every screen can build its ViewModel with a repository
 * argument, without pulling in a full dependency-injection framework.
 */
class GenericViewModelFactory<T : ViewModel>(
    private val creator: () -> T
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <U : ViewModel> create(modelClass: Class<U>): U = creator() as U
}
