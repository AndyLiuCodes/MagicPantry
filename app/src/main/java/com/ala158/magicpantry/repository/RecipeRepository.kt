package com.ala158.magicpantry.repository

import com.ala158.magicpantry.dao.RecipeDAO
import com.ala158.magicpantry.data.Recipe
import com.ala158.magicpantry.data.RecipeWithRecipeItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecipeRepository(private val recipeDAO: RecipeDAO) {
    val allRecipes: Flow<List<RecipeWithRecipeItems>> = recipeDAO.getAllRecipes()

    suspend fun insertRecipe(recipe: Recipe): Long {
        return recipeDAO.insertRecipe(recipe)
    }

    fun updateRecipe(recipe: Recipe) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeDAO.updateRecipe(recipe)
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeDAO.deleteRecipe(recipe)
        }
    }

    fun deleteRecipeById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeDAO.deleteRecipeById(id)
        }
    }
}
