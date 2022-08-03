package com.ala158.magicpantry.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ala158.magicpantry.data.*
import com.ala158.magicpantry.repository.RecipeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _newRecipeId = MutableLiveData(0L)
    val newRecipeId: LiveData<Long> = _newRecipeId

    val allRecipes: LiveData<List<RecipeWithRecipeItems>> = repository.allRecipes.asLiveData()
    val cookableRecipes = MutableLiveData<List<RecipeWithRecipeItems>>()

    var toBeAddedToRecipeIngredients: MutableMap<Long, Ingredient> = mutableMapOf()
    var addedRecipeItemAndIngredient = MutableLiveData<ArrayList<RecipeItemAndIngredient>>()
    var idToFilter = MutableLiveData<ArrayList<Int>>()

    init {
        addedRecipeItemAndIngredient.value = ArrayList()
    }

    suspend fun getRecipesById(keys: List<Long>): List<RecipeWithRecipeItems> {
        return repository.getRecipesById(keys)
    }

    fun insert(recipe: Recipe, recipeItemViewModel: RecipeItemViewModel) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = repository.insertRecipe(recipe)
            _newRecipeId.postValue(id)

            for (recipeItemAndIngredientEntry in addedRecipeItemAndIngredient.value!!.listIterator()) {
                recipeItemAndIngredientEntry.recipeItem.relatedRecipeId = id
                recipeItemViewModel.insert(recipeItemAndIngredientEntry.recipeItem)
            }
        }
    }

    fun update(recipe: Recipe) {
        repository.updateRecipe(recipe)
    }

    fun delete(recipe: Recipe) {
        repository.deleteRecipe(recipe)
    }

    fun updateCurrentCookable() {
        CoroutineScope(Dispatchers.IO).launch {
            val cookableArrayList = ArrayList<RecipeWithRecipeItems>()
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