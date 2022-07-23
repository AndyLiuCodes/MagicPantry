package com.ala158.magicpantry.dao

import androidx.room.*
import com.ala158.magicpantry.data.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDAO {
    @Insert
    suspend fun insertRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipe where recipeId = :key")
    suspend fun getRecipe(key: Long): Recipe

    @Query("SELECT * FROM recipe")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Query("DELETE FROM recipe where recipeId = :key")
    suspend fun deleteRecipeById(key: Long)
}