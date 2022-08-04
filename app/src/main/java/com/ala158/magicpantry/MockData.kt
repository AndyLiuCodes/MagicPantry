package com.ala158.magicpantry

import com.ala158.magicpantry.data.*
import java.util.*
import kotlin.collections.ArrayList


object MockData {

    val mockIngredients = arrayListOf(
        Ingredient("Bread", 10.0, "unit", 0.50),
        Ingredient("Milk", 2.0, "L", 1.00),
        Ingredient("Linguine", 5.0, "kg", 3.00),
        Ingredient("Tomato Sauce", 2.0, "L", 1.75),
        Ingredient("Ground Beef", 500.0, "g", 12.75),
        Ingredient("Flour", 10.0, "kg", 5.00),
        Ingredient("Orange Juice", 500.0, "mL", 2.50),
        Ingredient("Apple", 500.0, "g", 0.50),
        Ingredient("Potatoes", 1.0, "kg", 2.00),
        Ingredient("Water", 1000.0, "mL", 0.25)
    )

    val frenchToastRecipe = Recipe(
        title = "French Toast",
        servings = 2,
        timeToCook = 20,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi suscipit enim sed augue accumsan, vitae lacinia arcu aliquam. Nullam nunc mauris, commodo eget elit ut, pharetra ultrices est. Quisque auctor est justo, feugiat mollis orci aliquam non. Nunc vel nunc nec ex sagittis mollis. In a sagittis libero. Etiam condimentum, magna sit amet congue commodo, enim massa pulvinar enim, at blandit tortor augue sit amet sapien. Cras eu libero nec libero sagittis elementum. Nulla a enim ornare, venenatis nunc vel, congue metus. Maecenas eros lorem, dictum quis blandit ac, ultricies viverra nisl. Nunc ultricies euismod diam nec semper.",
    )

    fun generateFrenchToastIngredientList(ingredients: List<Ingredient>): ArrayList<RecipeItemAndIngredient> {
        return arrayListOf(
            RecipeItemAndIngredient(
                RecipeItem(
                    recipeAmount = 4.0,
                    relatedIngredientId = ingredients[0].ingredientId,
                    recipeUnit = "unit",
                ),
                ingredients[0]
            ),
            RecipeItemAndIngredient(
                RecipeItem(
                    recipeAmount = 100.0,
                    relatedIngredientId = ingredients[1].ingredientId,
                    recipeUnit = "mL",
                ),
                ingredients[1]
            ),
        )
    }

    val spaghettiRecipe = Recipe(
        title = "Spaghetti",
        servings = 4,
        timeToCook = 70,
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi suscipit enim sed augue accumsan, vitae lacinia arcu aliquam. Nullam nunc mauris, commodo eget elit ut, pharetra ultrices est. Quisque auctor est justo, feugiat mollis orci aliquam non. Nunc vel nunc nec ex sagittis mollis. In a sagittis libero. Etiam condimentum, magna sit amet congue commodo, enim massa pulvinar enim, at blandit tortor augue sit amet sapien. Cras eu libero nec libero sagittis elementum. Nulla a enim ornare, venenatis nunc vel, congue metus. Maecenas eros lorem, dictum quis blandit ac, ultricies viverra nisl. Nunc ultricies euismod diam nec semper.",
    )

    fun generateSpaghettiIngredientList(ingredients: List<Ingredient>): ArrayList<RecipeItemAndIngredient> {
        return arrayListOf(
            RecipeItemAndIngredient(
                RecipeItem(
                    recipeAmount = 0.5,
                    relatedIngredientId = ingredients[0].ingredientId,
                    recipeUnit = "kg",
                ),
                ingredients[0]
            ),
            RecipeItemAndIngredient(
                RecipeItem(
                    recipeAmount = 100.0,
                    relatedIngredientId = ingredients[1].ingredientId,
                    recipeUnit = "mL",
                ),
                ingredients[1]
            ),
            RecipeItemAndIngredient(
                RecipeItem(
                    recipeAmount = 100.0,
                    relatedIngredientId = ingredients[2].ingredientId,
                    recipeUnit = "g",
                ),
                ingredients[2]
            ),
        )
    }

    private fun oldDate(unit: Int, value: Int): Calendar {
        val oldDate = Calendar.getInstance()
        oldDate.set(unit, value)
        return oldDate
    }

    val notifications = arrayListOf(
        Notification(
            description = "Low on 2 ingredients",
            date = oldDate(Calendar.HOUR_OF_DAY, 5)
        ),
        Notification(
            description = "Low on Milk",
            isRead = true,
            date = oldDate(Calendar.MONTH, 1)
        ),
    )
}
