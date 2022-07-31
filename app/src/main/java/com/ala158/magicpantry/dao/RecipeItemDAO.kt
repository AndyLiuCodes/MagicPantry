package com.ala158.magicpantry.dao

import androidx.room.*
import com.ala158.magicpantry.data.RecipeItem
import com.ala158.magicpantry.data.RecipeItemWithRecipes
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeItemDAO {

    @Transaction
    @Query("SELECT * FROM recipe_item WHERE recipeItemId = :recipeItemId")
    fun getRecipeItemWithRecipes(recipeItemId: Long): Flow<List<RecipeItemWithRecipes>>

    @Transaction
    suspend fun insertRecipeItemIntoRecipe(recipeItem: RecipeItem, recipeId: Long) {
        val recipeItemId = insertRecipeItem(recipeItem)
        insertRecipeCrossRef(recipeId, recipeItemId)
    }

    @Insert
    suspend fun insertRecipeItem(recipeItem: RecipeItem): Long

    // Adding to the recipe item list
    @Query("INSERT INTO recipe_item_recipe_cross_ref (recipeId, recipeItemId) VALUES (:recipeId, :recipeItemId)")
    suspend fun insertRecipeCrossRef(recipeId: Long, recipeItemId: Long)

    @Delete
    suspend fun deleteRecipeItem(recipeItem: RecipeItem)

    @Update
    suspend fun updateRecipeItem(recipeItem: RecipeItem)

    // Deleting all recipe items from a recipe
    @Query("DELETE FROM recipe_item_recipe_cross_ref WHERE recipeId = :key")
    suspend fun deleteAllRecipeCrossRefById(key: Long)

    // Deleting from the recipe item list
    @Query("DELETE FROM recipe_item_recipe_cross_ref WHERE recipeId = :recipeId AND recipeItemId = :recipeItemId")
    suspend fun deleteRecipeCrossRefById(recipeId: Long, recipeItemId: Long)
}