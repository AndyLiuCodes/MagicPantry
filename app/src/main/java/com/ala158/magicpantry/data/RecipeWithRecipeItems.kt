package com.ala158.magicpantry.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RecipeWithRecipeItems(
    @Embedded val recipe: Recipe,
    @Relation(
        entity = RecipeItem::class,
        parentColumn = "recipeId",
        entityColumn = "recipeItemId",
        associateBy = Junction(RecipeItemRecipeCrossRef::class),
    )
    val recipeItems: List<RecipeItemAndIngredient>
)