package com.ala158.magicpantry.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ala158.magicpantry.data.RecipeItem
import com.ala158.magicpantry.data.RecipeItemAndRecipe
import com.ala158.magicpantry.repository.RecipeItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeItemViewModel(private val repository: RecipeItemRepository) : ViewModel() {

    private var _recipeItem: LiveData<RecipeItemAndRecipe> = MutableLiveData()
    val recipeItem: LiveData<RecipeItemAndRecipe> = _recipeItem

    fun getRecipesByRecipeItemId(recipeItemId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val recipeItem = repository.getRecipeItemWithRecipesById(recipeItemId).asLiveData()
            _recipeItem = recipeItem
        }
    }

    fun insert(recipeItem: RecipeItem) {
        repository.insertRecipeItemIntoRecipe(recipeItem)
    }

    fun delete(recipeItem: RecipeItem) {
        repository.deleteRecipeItem(recipeItem)
    }

    fun update(recipeItem: RecipeItem) {
        repository.updateRecipeItem(recipeItem)
    }
}