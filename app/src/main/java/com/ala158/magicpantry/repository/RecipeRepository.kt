package com.ala158.magicpantry.repository

import com.ala158.magicpantry.dao.RecipeDAO
import com.ala158.magicpantry.data.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecipeRepository(private val recipeDAO: RecipeDAO) {
    val allRecipes: Flow<List<Recipe>> = recipeDAO.getAllRecipes()

    suspend fun getRecipe(id: Long): Recipe {
        return recipeDAO.getRecipe(id)
    }

    fun insertRecipe(recipe: Recipe) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeDAO.insertRecipe(recipe)
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeDAO.deleteRecipe(recipe)
        }
    }

    fun updateRecipe(recipe: Recipe) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeDAO.updateRecipe(recipe)
        }
    }

    fun deleteRecipeById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeDAO.deleteRecipeById(id)
        }
    }
}
