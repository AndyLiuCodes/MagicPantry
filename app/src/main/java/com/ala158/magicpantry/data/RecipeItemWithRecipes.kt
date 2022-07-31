package com.ala158.magicpantry.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RecipeItemWithRecipes (
    @Embedded val recipeItem: RecipeItem,
    @Relation(
        parentColumn = "recipeItemId",
        entityColumn = "recipeId",
        associateBy = Junction(RecipeItemRecipeCrossRef::class)
    )
    val recipes: List<Recipe>
)