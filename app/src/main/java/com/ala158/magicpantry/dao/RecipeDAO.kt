package com.ala158.magicpantry.dao

import androidx.room.*
import com.ala158.magicpantry.data.Recipe
import com.ala158.magicpantry.data.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDAO {

    @Transaction
    @Query("SELECT * FROM recipe")
    fun getAllRecipes(): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * FROM recipe WHERE recipeId = :key")
    suspend fun getRecipe(key: Long): RecipeWithIngredients

    @Insert
    suspend fun insertRecipe(recipe: Recipe): Long

    // Adding to the ingredient list
    @Query("INSERT INTO ingredient_recipe_cross_ref (ingredientId, recipeId) VALUES (:ingredientId, :recipeId)")
    suspend fun insertRecipeCrossRef(recipeId: Long, ingredientId: Long)

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Query("DELETE FROM recipe where recipeId = :key")
    suspend fun deleteRecipeById(key: Long)

    // Deleting all ingredients from a recipe
    @Query("DELETE FROM ingredient_recipe_cross_ref where recipeId = :key")
    suspend fun deleteAllRecipeCrossRefById(key: Long)

    // Deleting from the ingredient list
    @Query("DELETE FROM ingredient_recipe_cross_ref where recipeId = :recipeId AND ingredientId = :ingredientId")
    suspend fun deleteRecipeCrossRefById(recipeId: Long, ingredientId: Long)
}