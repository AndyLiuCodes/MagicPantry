package com.ala158.magicpantry.data

import androidx.room.Embedded

data class ShoppingListItemAndIngredient(
    @Embedded val shoppingListItem: ShoppingListItem,
    @Embedded val ingredient: Ingredient,
)
