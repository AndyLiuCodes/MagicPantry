package com.ala158.magicpantry.data

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeItemAndRecipe(
    @Embedded val recipeItem: RecipeItem,
    @Relation(
        entity = Recipe::class,
        parentColumn = "recipeItemId",
        entityColumn = "recipeId",
    )
    val recipe: RecipeWithRecipeItems
)