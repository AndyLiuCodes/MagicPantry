package com.ala158.magicpantry.data

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeItemAndIngredient(
    @Embedded val recipeItem: RecipeItem,
    @Relation(
        parentColumn = "recipeItemId",
        entityColumn = "ingredientId"
    )
    val ingredient: Ingredient,
)
