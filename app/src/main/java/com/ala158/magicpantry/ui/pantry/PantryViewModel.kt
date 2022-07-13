package com.ala158.magicpantry.ui.pantry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.repository.MagicPantryRepository

class PantryViewModel(private val repository: MagicPantryRepository) : ViewModel() {
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