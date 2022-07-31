package com.ala158.magicpantry.data

import androidx.room.Entity

@Entity(tableName = "recipe_item_recipe_cross_ref",
        primaryKeys = ["recipeItemId", "recipeId"])
data class RecipeItemRecipeCrossRef(
    val recipeItemId: Long,
    val recipeId: Long,
)
