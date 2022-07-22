package com.ala158.magicpantry.data

import androidx.room.Entity

@Entity(primaryKeys = ["ingredientId", "recipeId"])
data class IngredientRecipeCrossRef(
    var ingredientId: Long,
    var recipeId: Long,
)
