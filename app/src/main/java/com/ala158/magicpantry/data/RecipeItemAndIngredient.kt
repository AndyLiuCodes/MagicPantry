package com.ala158.magicpantry.data

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeItemAndIngredient(
    @Embedded val recipeItem: RecipeItem,
    @Relation(
        parentColumn = "related_ingredient_id",
        entityColumn = "ingredientId"
    )
    val ingredient: Ingredient,
)
