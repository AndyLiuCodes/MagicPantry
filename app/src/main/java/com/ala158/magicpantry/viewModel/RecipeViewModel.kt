package com.ala158.magicpantry.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.Recipe
import com.ala158.magicpantry.repository.IngredientRepository
import com.ala158.magicpantry.repository.RecipeRepository

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {
    val allRecipeLiveData: LiveData<List<Recipe>> = repository.allRecipes.asLiveData()

    fun insert(recipe: Recipe) {
        repository.insertRecipe(recipe)
    }

    fun delete(recipe: Recipe){
        repository.deleteRecipe(recipe)
    }

    fun update(recipe: Recipe){
        repository.updateRecipe(recipe)
    }
}

