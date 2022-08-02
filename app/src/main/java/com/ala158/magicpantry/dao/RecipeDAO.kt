package com.ala158.magicpantry.dao

import androidx.room.*
import com.ala158.magicpantry.data.Recipe
import com.ala158.magicpantry.data.RecipeWithRecipeItems
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDAO {

    @Transaction
    @Query("SELECT * FROM recipe")
    fun getAllRecipes(): Flow<List<RecipeWithRecipeItems>>

    @Transaction
    @Query("SELECT * FROM recipe WHERE recipeId IN (:keys)")
    fun getRecipesById(keys: List<Long>): Flow<List<RecipeWithRecipeItems>>

    @Insert
    suspend fun insertRecipe(recipe: Recipe): Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Query("DELETE FROM recipe WHERE recipeId = :key")
    suspend fun deleteRecipeById(key: Long)

}