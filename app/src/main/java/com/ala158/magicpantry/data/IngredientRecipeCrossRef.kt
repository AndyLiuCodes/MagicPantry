package com.ala158.magicpantry.data

import androidx.room.Entity

@Entity(tableName = "ingredient_recipe_cross_ref", primaryKeys = ["ingredientId", "recipeId"])
data class IngredientRecipeCrossRef(
    val ingredientId: Long,
    val recipeId: Long,
)
