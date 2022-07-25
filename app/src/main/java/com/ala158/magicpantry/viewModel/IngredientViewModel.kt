package com.ala158.magicpantry.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.repository.IngredientRepository

class IngredientViewModel(private val repository: IngredientRepository) : ViewModel() {
    val allIngredientsLiveData: LiveData<List<Ingredient>> = repository.allIngredients.asLiveData()

    fun insert(ingredient: Ingredient) {
        repository.insertIngredient(ingredient)
    }

    fun delete(ingredient: Ingredient){
        repository.deleteIngredient(ingredient)
    }

    fun update(ingredient: Ingredient){
        repository.updateIngredient(ingredient)
    }
}

