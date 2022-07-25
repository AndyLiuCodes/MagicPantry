package com.ala158.magicpantry.repository

import com.ala158.magicpantry.dao.IngredientDAO
import com.ala158.magicpantry.data.Ingredient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class IngredientRepository(private val ingredientDAO: IngredientDAO) {
    val allIngredients: Flow<List<Ingredient>> = ingredientDAO.getAllIngredients()

    suspend fun getIngredient(id: Long): Ingredient {
        return ingredientDAO.getIngredientEntry(id)
    }

    suspend fun getIngredientByNameAndUnit(name: String, unit: String): Ingredient? {
        return ingredientDAO.getIngredientEntryByNameAndUnit(name, unit)
    }

    fun insertIngredient(ingredient: Ingredient) {
        CoroutineScope(IO).launch {
            ingredientDAO.insertIngredient(ingredient)
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        CoroutineScope(IO).launch {
            ingredientDAO.deleteIngredient(ingredient)
        }
    }

    fun updateIngredient(ingredient: Ingredient) {
        CoroutineScope(IO).launch {
            ingredientDAO.updateIngredient(ingredient)
        }
    }

    fun deleteIngredientById(id: Long) {
        CoroutineScope(IO).launch {
            ingredientDAO.deleteIngredientById(id)
        }
    }
}