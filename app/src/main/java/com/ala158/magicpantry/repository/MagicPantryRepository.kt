package com.ala158.magicpantry.repository

import com.ala158.magicpantry.dao.IngredientDAO
import com.ala158.magicpantry.data.Ingredient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MagicPantryRepository(private val ingredientDAO: IngredientDAO) {

    val allIngredients: Flow<List<Ingredient>> = ingredientDAO.getAllIngredients()

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
}