package com.ala158.magicpantry.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ala158.magicpantry.data.RecipeItemWithRecipes
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeItemDAO {

    @Transaction
    @Query("SELECT * FROM recipe_item WHERE recipeItemId = :recipeItemId")
    fun getAllRecipes(recipeItemId: Long): Flow<List<RecipeItemWithRecipes>>

}