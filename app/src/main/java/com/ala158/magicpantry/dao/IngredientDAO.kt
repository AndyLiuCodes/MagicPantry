package com.ala158.magicpantry.dao

import androidx.room.*
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.IngredientWithRecipeItems
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDAO {

    @Insert
    suspend fun insertIngredient(ingredient: Ingredient)

    @Insert
    suspend fun insertIngredients(ingredients: List<Ingredient>)

    @Insert
    suspend fun insertIngredientReturnId(ingredient: Ingredient): Long

    @Query("SELECT * FROM ingredient WHERE ingredientId = :key")
    suspend fun getIngredientEntry(key: Long): Ingredient

    @Query("SELECT * FROM ingredient WHERE name = :nameKey AND unit = :unitKey")
    suspend fun getIngredientEntryByNameAndUnit(nameKey: String, unitKey: String): Ingredient?

    @Query("SELECT * FROM ingredient ORDER BY ingredientId ASC")
    fun getAllIngredients(): Flow<List<Ingredient>>

    @Transaction
    @Query("SELECT * FROM ingredient WHERE ingredientId IN (:keys)")
    suspend fun getIngredientsWithRecipeItemsById(keys: List<Long>): List<IngredientWithRecipeItems>

    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    @Update
    suspend fun updateIngredient(ingredient: Ingredient)

    @Update
    fun updateIngredientSync(ingredient: Ingredient)

    @Query("DELETE FROM ingredient WHERE ingredientId = :key")
    suspend fun deleteIngredientById(key: Long)

    @Query("DELETE FROM ingredient WHERE ingredientId = :key")
    fun deleteIngredientByIdSync(key: Long)
}