package com.ala158.magicpantry.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ala158.magicpantry.arrayAdapter.RecipeListArrayAdapter
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.Recipe
import com.ala158.magicpantry.data.RecipeWithIngredients
import com.ala158.magicpantry.repository.RecipeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _newRecipeId = MutableLiveData(0L)
    val newRecipeId: LiveData<Long> = _newRecipeId

    val allRecipes: LiveData<List<RecipeWithIngredients>> = repository.allRecipes.asLiveData()
    val cookableRecipes = MutableLiveData<List<RecipeWithIngredients>>()

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

    fun updateCurrentCookable() {
        CoroutineScope(Dispatchers.IO).launch {
            val cookableArrayList = ArrayList<RecipeWithIngredients>()
            if (allRecipes.value != null) {
                for (currRecipe in allRecipes.value!!) {
                    if (currRecipe.recipe.numMissingIngredients == 0) {
                        cookableArrayList.add(currRecipe)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                cookableRecipes.value = cookableArrayList
            }
        }
    }

}