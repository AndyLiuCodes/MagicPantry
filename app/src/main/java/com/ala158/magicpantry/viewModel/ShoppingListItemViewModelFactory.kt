package com.ala158.magicpantry.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ala158.magicpantry.repository.RecipeRepository
import com.ala158.magicpantry.repository.ShoppingListItemRepository
import java.lang.IllegalArgumentException

class ShoppingListItemViewModelFactory(private val repository: ShoppingListItemRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingListItemViewModel::class.java))
            return ShoppingListItemViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}