package com.ala158.magicpantry.data

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithRecipeItems(
    @Embedded val recipe: Recipe,
    @Relation(
        entity = RecipeItem::class,
        parentColumn = "recipeId",
        entityColumn = "related_recipe_id",
    )
    val recipeItems: List<RecipeItemAndIngredient>
)
