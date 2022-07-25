package com.ala158.magicpantry.repository

import com.ala158.magicpantry.dao.RecipeDAO
import com.ala158.magicpantry.data.Recipe
import com.ala158.magicpantry.data.RecipeWithIngredients
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecipeRepository(private val recipeDAO: RecipeDAO) {
    val allRecipes: Flow<List<RecipeWithIngredients>> = recipeDAO.getAllRecipes()

    suspend fun insertRecipe(recipe: Recipe): Long {
        return recipeDAO.insertRecipe(recipe)
    }

    fun insertRecipeCrossRef(recipeId: Long, ingredientId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeDAO.insertRecipeCrossRef(recipeId, ingredientId)
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

    fun deleteAllRecipeCrossRef(recipeId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeDAO.deleteAllRecipeCrossRefById(recipeId)
        }
    }

    fun deleteRecipeCrossRef(recipeId: Long, ingredientId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            recipeDAO.deleteRecipeCrossRefById(recipeId, ingredientId)
        }
    }
}
