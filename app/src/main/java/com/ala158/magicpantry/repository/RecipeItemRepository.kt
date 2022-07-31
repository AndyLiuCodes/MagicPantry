package com.ala158.magicpantry.repository

import com.ala158.magicpantry.dao.RecipeItemDAO
import com.ala158.magicpantry.data.RecipeItem
import com.ala158.magicpantry.data.RecipeItemWithRecipes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecipeItemRepository(private val recipeItemDAO: RecipeItemDAO) {

    fun getRecipesByRecipeItemId(recipeItemId: Long): Flow<List<RecipeItemWithRecipes>> {
        return recipeItemDAO.getRecipeItemWithRecipes(recipeItemId)
    }

    fun insertRecipeItem(recipeItem: RecipeItem) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeItemDAO.insertRecipeItem(recipeItem)
        }
    }

    fun deleteRecipeItem(recipeItem: RecipeItem) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeItemDAO.deleteRecipeItem(recipeItem)
        }
    }

    fun updateRecipeItem(recipeItem: RecipeItem) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeItemDAO.updateRecipeItem(recipeItem)
        }
    }
}