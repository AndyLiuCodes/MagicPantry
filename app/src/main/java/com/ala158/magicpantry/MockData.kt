package com.ala158.magicpantry

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
}