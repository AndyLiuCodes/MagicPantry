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

    @Insert
    fun insertRecipeItem(recipeItem: RecipeItem)

    @Delete
    fun deleteRecipeItem(recipeItem: RecipeItem)

    @Update
    fun updateRecipeItem(recipeItem: RecipeItem)

}