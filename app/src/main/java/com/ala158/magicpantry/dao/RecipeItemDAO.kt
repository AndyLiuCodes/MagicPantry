package com.ala158.magicpantry.dao

import androidx.room.*
import com.ala158.magicpantry.data.RecipeItem
import com.ala158.magicpantry.data.RecipeItemAndRecipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeItemDAO {

    @Transaction
    @Query("SELECT * FROM recipe_item WHERE recipeItemId = :recipeItemId")
    fun getRecipeItemWithRecipesById(recipeItemId: Long): Flow<RecipeItemAndRecipe>

    @Insert
    suspend fun insertRecipeItem(recipeItem: RecipeItem): Long

    @Delete
    suspend fun deleteRecipeItem(recipeItem: RecipeItem)

    @Update
    suspend fun updateRecipeItem(recipeItem: RecipeItem)

    @Update
    fun updateRecipeItemSync(recipeItem: RecipeItem)
}