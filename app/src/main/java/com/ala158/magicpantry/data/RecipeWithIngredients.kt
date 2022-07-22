package com.ala158.magicpantry.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RecipeWithIngredients(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "ingredientId",
        associateBy = Junction(IngredientRecipeCrossRef::class)
    )
    val ingredients: List<Ingredient>
)
