package com.ala158.magicpantry.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ala158.magicpantry.repository.MagicPantryRepository
import java.lang.IllegalArgumentException

class ViewModelFactory (private val repository: MagicPantryRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(IngredientViewModel::class.java))
            return IngredientViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}