package com.ala158.magicpantry.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ala158.magicpantry.repository.RecipeRepository
import java.lang.IllegalArgumentException

class RecipeViewModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java))
            return RecipeViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}