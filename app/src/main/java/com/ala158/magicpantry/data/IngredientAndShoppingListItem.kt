package com.ala158.magicpantry.data

import androidx.room.Embedded
import androidx.room.Relation

data class IngredientAndShoppingListItem(
    @Embedded val ingredient: Ingredient,
    @Relation(
        parentColumn = "ingredientId",
        entityColumn = "related_ingredient_id"
    )
    val shoppingListItem: ShoppingListItem
)
