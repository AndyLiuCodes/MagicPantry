package com.ala158.magicpantry

import com.ala158.magicpantry.arrayAdapter.ShoppingListItem
import com.ala158.magicpantry.data.Ingredient

object MockData {
    val ingredients = arrayListOf(
        Ingredient("Banana", 3, "unit", 0.50),
        Ingredient("Apple", amount = 1, unit = "unit", price = 0.20),
        Ingredient("Linguine", 300, "grams", 5.00),
        Ingredient("Tomato Sauce", 1, "litre", 10.00),
        Ingredient("Sugar", 100, "grams", 10.00),
    )

    val lowIngredients = arrayListOf(
        Ingredient("Banana", 3, "unit", 0.50),
        Ingredient("Apple", 1, "unit", 0.20),
    )

    val shoppingList = arrayListOf(
        ShoppingListItem("Bread", 100, "Units", false),
        ShoppingListItem("Corn", 5, "Units", true),
        ShoppingListItem("Flour", 10, "Kg", false),
        ShoppingListItem("Milk", 2, "L", true),
        ShoppingListItem("Orange Juice", 500, "ml", false),
        ShoppingListItem("Apple", 500, "g", false),
        ShoppingListItem("Potatos", 1, "Kg", false),
        ShoppingListItem("Water", 1000, "ml", false)
    )
}