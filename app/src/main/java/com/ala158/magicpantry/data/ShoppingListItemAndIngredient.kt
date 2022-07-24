package com.ala158.magicpantry.data

import androidx.room.Embedded
import androidx.room.Relation

data class ShoppingListItemAndIngredient(
    @Embedded val shoppingListItem: ShoppingListItem,
    @Relation(
        parentColumn = "shoppingListItemId",
        entityColumn = "related_shopping_list_item_id"
    )
    val ingredient: Ingredient,
)
