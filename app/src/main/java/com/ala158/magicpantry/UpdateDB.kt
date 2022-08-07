package com.ala158.magicpantry

import android.util.Log
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.Notification
import com.ala158.magicpantry.data.RecipeWithRecipeItems
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.NotificationViewModel
import com.ala158.magicpantry.viewModel.RecipeItemViewModel
import com.ala158.magicpantry.viewModel.RecipeViewModel
import java.util.*

object UpdateDB {
    fun consumeIngredients(
        recipe: RecipeWithRecipeItems,
        parentIngredientViewModel: IngredientViewModel
    ) {
        // Get all recipeItems
        val recipeItems = recipe.recipeItems

        // Update the pantry ingredient amounts
        for (item in recipeItems) {
            // Need to handle unit conversion
            val convertedUnitAmount = Util.unitConversion(
                item.recipeItem.recipeAmount,
                item.ingredient.unit,
                item.recipeItem.recipeUnit
            )
            item.ingredient.amount -= convertedUnitAmount
            parentIngredientViewModel.updateSync(item.ingredient)
        }
    }

    suspend fun postUpdatesAfterModifyIngredient(
        updatedIngredientIds: List<Long>,
        parentIngredientViewModel: IngredientViewModel,
        parentRecipeItemViewModel: RecipeItemViewModel,
        parentRecipeViewModel: RecipeViewModel
    ) {
        val updatedWithItemsNotEnough = updateRecipeItemsNotEnough(
            updatedIngredientIds,
            parentIngredientViewModel,
            parentRecipeItemViewModel,
        )
        updateRecipesMissingIngredients(
            updatedWithItemsNotEnough,
            parentRecipeViewModel
        )
    }

    private suspend fun updateRecipeItemsNotEnough(
        updatedIngredientIds: List<Long>,
        parentIngredientViewModel: IngredientViewModel,
        parentRecipeItemViewModel: RecipeItemViewModel,
    ): List<Long> {
        val recipeIdsToUpdate = mutableSetOf<Long>()
        val ingredientsWithRecipeItems =
            parentIngredientViewModel.findIngredientsWithRecipeItemsById(updatedIngredientIds)

        Log.d(
            "INGREDIENT",
            "updateRecipeItemsNotEnough: ingredients ${ingredientsWithRecipeItems.size}"
        )
        for (ingredientWithRecipeItem in ingredientsWithRecipeItems) {
            // Update all recipe items if the amount is enough to cook with
            Log.d(
                "INGREDIENT",
                "updateRecipeItemsNotEnough: number of recipeitems for an ingredient ${ingredientWithRecipeItem.recipeItems.size}"
            )
            for (item in ingredientWithRecipeItem.recipeItems) {
                val convertedRecipeAmount = Util.unitConversion(
                    item.recipeAmount,
                    ingredientWithRecipeItem.ingredient.unit,
                    item.recipeUnit
                )
                item.recipeIsEnough =
                    convertedRecipeAmount <= ingredientWithRecipeItem.ingredient.amount
                recipeIdsToUpdate.add(item.relatedRecipeId)
                Log.d(
                    "INGREDIENT",
                    "updateRecipesMissingIngredients: ${item.recipeIsEnough} ${ingredientWithRecipeItem.ingredient.name}"
                )
                parentRecipeItemViewModel.updateSync(item)
            }
        }
        return recipeIdsToUpdate.toList()
    }

    private suspend fun updateRecipesMissingIngredients(
        recipeIdsToUpdate: List<Long>,
        parentRecipeViewModel: RecipeViewModel
    ) {
        // Update all recipes' num of missing ingredients that have had their ingredients updated
        val recipes = parentRecipeViewModel.getRecipesById(recipeIdsToUpdate)

        for (recipe in recipes) {
            var count = 0
            for (recipeItem in recipe.recipeItems) {
                if (!recipeItem.recipeItem.recipeIsEnough) {
                    Log.d(
                        "INGREDIENT",
                        "updateRecipesMissingIngredients: ${recipeItem.ingredient.name}"
                    )
                    count += 1
                }
            }
            // Update the recipe's number of missing ingredients
            recipe.recipe.numMissingIngredients = count
            parentRecipeViewModel.update(recipe.recipe)
        }
    }
}