package com.ala158.magicpantry.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ala158.magicpantry.data.RecipeItem
import com.ala158.magicpantry.data.RecipeItemWithRecipes
import com.ala158.magicpantry.repository.RecipeItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeItemViewModel(private val repository: RecipeItemRepository) : ViewModel() {

    private var _allRecipes: LiveData<List<RecipeItemWithRecipes>> = MutableLiveData()
    val allRecipes: LiveData<List<RecipeItemWithRecipes>> = _allRecipes

    private val _newRecipeItemId = MutableLiveData(0L)
    val newRecipeItemId: LiveData<Long> = _newRecipeItemId

    fun getRecipesByRecipeItemId(recipeItemId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val recipes = repository.getRecipesByRecipeItemId(recipeItemId).asLiveData()
            _allRecipes = recipes
        }
    }

    fun insert(recipeItem: RecipeItem, recipeId: Long) {
        repository.insertRecipeItemIntoRecipe(recipeItem, recipeId)
    }

    fun delete(recipeItem: RecipeItem) {
        repository.deleteRecipeItem(recipeItem)
    }

    fun update(recipeItem: RecipeItem) {
        repository.updateRecipeItem(recipeItem)
    }
}