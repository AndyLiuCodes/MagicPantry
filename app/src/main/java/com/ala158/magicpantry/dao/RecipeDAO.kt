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

    @Insert
    suspend fun insertRecipe(recipe: Recipe): Long

    // Adding to the recipe item list
    @Query("INSERT INTO recipe_item_recipe_cross_ref (recipeId, recipeItemId) VALUES (:recipeId, :recipeItemId)")
    suspend fun insertRecipeCrossRef(recipeId: Long, recipeItemId: Long)

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Query("DELETE FROM recipe WHERE recipeId = :key")
    suspend fun deleteRecipeById(key: Long)

    // Deleting all recipe items from a recipe
    @Query("DELETE FROM recipe_item_recipe_cross_ref WHERE recipeId = :key")
    suspend fun deleteAllRecipeCrossRefById(key: Long)

    // Deleting from the recipe item list
    @Query("DELETE FROM recipe_item_recipe_cross_ref WHERE recipeId = :recipeId AND recipeItemId = :recipeItemId")
    suspend fun deleteRecipeCrossRefById(recipeId: Long, recipeItemId: Long)
}