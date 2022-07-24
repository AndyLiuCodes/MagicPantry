package com.ala158.magicpantry.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ala158.magicpantry.data.Recipe
import com.ala158.magicpantry.data.RecipeWithIngredients
import com.ala158.magicpantry.repository.RecipeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Recipes Fragment"
    }
    val text: LiveData<String> = _text

    private val _newRecipeId = MutableLiveData(0L)
    val newRecipeId: LiveData<Long> = _newRecipeId

    val allRecipes: LiveData<List<RecipeWithIngredients>> = repository.allRecipes.asLiveData()

    fun insert(recipe: Recipe) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = repository.insertRecipe(recipe)
            _newRecipeId.postValue(id)
        }
    }

    fun insertCrossRef(recipeId: Long, ingredientId: Long) {
        repository.insertRecipeCrossRef(recipeId, ingredientId)
    }

    fun update(recipe: Recipe) {
        repository.updateRecipe(recipe)
    }

    fun deleteById(key: Long) {
        repository.deleteRecipeById(key)
        repository.deleteAllRecipeCrossRef(key)
    }

    fun deleteCrossRef(recipeId: Long, ingredientId: Long) {
        repository.deleteRecipeCrossRef(recipeId, ingredientId)
    }
}