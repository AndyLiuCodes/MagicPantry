package com.ala158.magicpantry.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ala158.magicpantry.data.RecipeItem
import com.ala158.magicpantry.data.RecipeItemAndRecipe
import com.ala158.magicpantry.repository.RecipeItemRepository

class RecipeItemViewModel(private val repository: RecipeItemRepository) : ViewModel() {
    val allRecipeItems: LiveData<List<RecipeItem>> = repository.allRecipeItems.asLiveData()

    private var _recipeItem: LiveData<RecipeItemAndRecipe> = MutableLiveData()
    val recipeItem: LiveData<RecipeItemAndRecipe> = _recipeItem

    fun insert(recipeItem: RecipeItem) {
        repository.insertRecipeItemIntoRecipe(recipeItem)
    }

    fun insertListSync(recipeItems: List<RecipeItem>) {
        repository.insertRecipeItemListSync(recipeItems)
    }

    fun insertSync(recipeItem: RecipeItem) {
        repository.insertRecipeItemIntoRecipeSync(recipeItem)
    }

    fun delete(recipeItem: RecipeItem) {
        repository.deleteRecipeItem(recipeItem)
    }

    fun deleteListSync(recipeItems: List<RecipeItem>) {
        repository.deleteRecipeItemListSync(recipeItems)
    }

    fun deleteById(id: Long) {
        repository.deleteRecipeItemById(id)
    }

    fun update(recipeItem: RecipeItem) {
        repository.updateRecipeItem(recipeItem)
    }

    fun updateListSync(recipeItems: List<RecipeItem>) {
        repository.updateRecipeItemListSync(recipeItems)
    }

    fun updateSync(recipeItem: RecipeItem) {
        repository.updateRecipeItemSync(recipeItem)
    }
}