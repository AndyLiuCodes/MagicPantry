package com.ala158.magicpantry

import com.ala158.magicpantry.arrayAdapter.ShoppingListItem
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.Recipe
import com.ala158.magicpantry.data.ShoppingListItem

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
    val recipe = Recipe("French Toast", 2, 20, "Testing description")

    val allIngredientsToastTest = arrayListOf(
        Ingredient("Bread", 4, "unit", 0.50),
        Ingredient("Milk", 2, "L", 1.00),
        Ingredient("Cinnamon", 5, "g", 2.00),
    )

    val shoppingListItems = arrayListOf(
        ShoppingListItem(
            itemName = allIngredientsToastTest[0].name,
            itemAmount = 10,
            itemUnit = "unit"
        ),
        ShoppingListItem(
            itemName = allIngredientsToastTest[1].name,
            itemAmount = 1,
            itemUnit = "L"
        ),
    )

}