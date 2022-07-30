package com.ala158.magicpantry

import com.ala158.magicpantry.data.*
import java.util.*


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

    val recipe2 = Recipe(title = "French Toast", servings = 2, timeToCook = 20, description = "Testing description", numMissingIngredients = 2, recipeId = 600)
    val recipeIngredient =  Ingredient(name = "Banana", amount = 1, unit = "unit", price = 0.20, ingredientId = 400)
    val recipe2 = Recipe(
        title = "French Toast",
        servings = 2,
        timeToCook = 20,
        description = "Testing description",
        numMissingIngredients = 2
    )

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

    val notification = Notification(description = "Running low on bread")

    private fun oldDate(unit: Int, value: Int): Calendar {
        val oldDate = Calendar.getInstance()
        oldDate.set(unit, value)
        return oldDate
    }

    val notifications = arrayListOf(
        NotificationWithIngredients(
            Notification(
                description = "Low on 2 ingredients",
                date = oldDate(Calendar.HOUR_OF_DAY, 5)
            ),
            arrayListOf(
                allIngredientsToastTest[0],
                allIngredientsToastTest[1],
            )
        ),
        NotificationWithIngredients(
            Notification(
                description = "Low on Cinnamon",
                isRead = true,
                date = oldDate(Calendar.MONTH, 1)
            ),
            arrayListOf(
                allIngredientsToastTest[2]
            )
        ),
    )
}
