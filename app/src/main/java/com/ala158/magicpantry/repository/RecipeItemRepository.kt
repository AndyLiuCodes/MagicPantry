package com.ala158.magicpantry.repository

import com.ala158.magicpantry.dao.RecipeItemDAO
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.RecipeItem
import com.ala158.magicpantry.data.RecipeItemAndRecipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecipeItemRepository(private val recipeItemDAO: RecipeItemDAO) {
    val allRecipeItems: Flow<List<RecipeItem>> = recipeItemDAO.getAllRecipeItems()

    fun getRecipeItemWithRecipesById(recipeItemId: Long): Flow<RecipeItemAndRecipe> {
        return recipeItemDAO.getRecipeItemWithRecipesById(recipeItemId)
    }

    fun insertRecipeItemIntoRecipe(recipeItem: RecipeItem) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeItemDAO.insertRecipeItem(recipeItem)
        }
    }

    fun insertAllRecipeItemsIntoRecipe(recipeItems: List<RecipeItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeItemDAO.insertAllRecipeItems(recipeItems)
        }
    }

    fun insertRecipeItemIntoRecipeSync(recipeItem: RecipeItem) {
        recipeItemDAO.insertRecipeItemSync(recipeItem)
    }

    fun deleteRecipeItem(recipeItem: RecipeItem) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeItemDAO.deleteRecipeItem(recipeItem)
        }
    }

    fun deleteRecipeItemById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeItemDAO.deleteRecipeItemById(id)
        }
    }

    fun updateRecipeItem(recipeItem: RecipeItem) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeItemDAO.updateRecipeItem(recipeItem)
        }
    }

    fun updateRecipeItemSync(recipeItem: RecipeItem) {
        recipeItemDAO.updateRecipeItemSync(recipeItem)
    }
}