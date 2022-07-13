package com.ala158.magicpantry.ui.pantry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ala158.magicpantry.repository.MagicPantryRepository
import java.lang.IllegalArgumentException

class PantryViewModelFactory(private val repository: MagicPantryRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(PantryViewModel::class.java))
            return PantryViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}