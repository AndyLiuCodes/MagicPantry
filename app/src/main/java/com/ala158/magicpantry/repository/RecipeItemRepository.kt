package com.ala158.magicpantry.repository

import com.ala158.magicpantry.dao.RecipeItemDAO
import com.ala158.magicpantry.data.RecipeItemWithRecipes
import kotlinx.coroutines.flow.Flow

class RecipeItemRepository(private val recipeItemDAO: RecipeItemDAO) {

    fun getRecipesByRecipeItemId(recipeItemId: Long): Flow<List<RecipeItemWithRecipes>> {
        return recipeItemDAO.getAllRecipes(recipeItemId)
    }

}