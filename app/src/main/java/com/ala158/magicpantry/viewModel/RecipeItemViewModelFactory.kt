package com.ala158.magicpantry.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ala158.magicpantry.repository.RecipeItemRepository

class RecipeItemViewModelFactory(private val repository: RecipeItemRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeItemViewModel::class.java))
            return RecipeItemViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}