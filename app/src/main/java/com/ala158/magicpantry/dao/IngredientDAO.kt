package com.ala158.magicpantry.dao

import androidx.room.*
import com.ala158.magicpantry.data.Ingredient
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDAO {

    @Insert
    suspend fun insertIngredient(ingredient: Ingredient)

    @Query("SELECT * FROM ingredient where ingredientId = :key")
    suspend fun getIngredientEntry(key: Long): Ingredient

    @Query("SELECT * FROM ingredient")
    fun getAllIngredients(): Flow<List<Ingredient>>

    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    @Update
    suspend fun updateIngredient(ingredient: Ingredient)

    @Query("DELETE FROM ingredient where ingredientId = :key")
    suspend fun deleteIngredientById(key: Long)
}