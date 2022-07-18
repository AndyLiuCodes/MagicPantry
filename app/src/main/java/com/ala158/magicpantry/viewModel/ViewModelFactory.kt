package com.ala158.magicpantry.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ala158.magicpantry.repository.MagicPantryRepository
import com.ala158.magicpantry.ui.manualingredientinput.ManualIngredientInputViewModel
import com.ala158.magicpantry.ui.manualingredientinput.edit.PantryEditIngredientViewModel
import com.ala158.magicpantry.ui.pantry.PantryViewModel
import com.ala158.magicpantry.ui.reviewingredients.ReviewIngredientsViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(private val repository: MagicPantryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IngredientViewModel::class.java))
            return IngredientViewModel(repository) as T
        else if (modelClass.isAssignableFrom(ReviewIngredientsViewModel::class.java))
            return ReviewIngredientsViewModel(repository) as T
        else if (modelClass.isAssignableFrom(PantryViewModel::class.java))
            return PantryViewModel(repository) as T
        else if (modelClass.isAssignableFrom(ManualIngredientInputViewModel::class.java))
            return ManualIngredientInputViewModel(repository) as T
        else if (modelClass.isAssignableFrom(PantryEditIngredientViewModel::class.java))
            return PantryEditIngredientViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}