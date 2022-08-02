package com.ala158.magicpantry

import android.util.Log
import com.ala158.magicpantry.data.RecipeWithRecipeItems
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.RecipeItemViewModel
import com.ala158.magicpantry.viewModel.RecipeViewModel

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
            Log.d(
                "SINGLE_RECIPE",
                "consumeIngredients: Ingredient: ${item.ingredient.name} CURR AMT: " +
                        "${item.ingredient.amount}"
            )
            item.ingredient.amount -= convertedUnitAmount
            Log.d(
                "SINGLE_RECIPE",
                "consumeIngredients: Ingredient: ${item.ingredient.name} New AMT: " +
                        "${item.ingredient.amount}"
            )
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

        for (ingredientWithRecipeItem in ingredientsWithRecipeItems) {

            Log.d(
                "SINGLE_RECIPE",
                "updateRecipesMissingIngredients: ingredient " +
                        ingredientWithRecipeItem.ingredient.name
            )

            // Update all recipe items if the amount is enough to cook with
            for (item in ingredientWithRecipeItem.recipeItems) {
                Log.d(
                    "SINGLE_RECIPE",
                    "updateRecipesMissingIngredients: RecipeItemAMT: ${item.recipeAmount} " +
                            "IngredientAMT: ${ingredientWithRecipeItem.ingredient.amount}"
                )
                val convertedRecipeAmount = Util.unitConversion(
                    item.recipeAmount,
                    ingredientWithRecipeItem.ingredient.unit,
                    item.recipeUnit
                )
                item.recipeIsEnough = convertedRecipeAmount <= ingredientWithRecipeItem.ingredient.amount
                recipeIdsToUpdate.add(item.relatedRecipeId)
                Log.d(
                    "SINGLE_RECIPE",
                    "updateRecipesMissingIngredients: isEnough: ${item.recipeIsEnough}"
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
        // Update all recipes' num of missing ingredients that have had their recipeItems
        // isEnough set to false
        val recipes = parentRecipeViewModel.getRecipesById(recipeIdsToUpdate)

        for (recipe in recipes) {
            Log.d(
                "SINGLE_RECIPE",
                "updateRecipesMissingIngredients: recipeItem: ${recipe.recipe.title}"
            )
            var count = 0
            for (recipeItem in recipe.recipeItems) {
                if (!recipeItem.recipeItem.recipeIsEnough) {
                    count += 1
                    Log.d(
                        "SINGLE_RECIPE",
                        "updateRecipesMissingIngredients: ${recipeItem.recipeItem} not enough"
                    )
                }
            }

            Log.d(
                "SINGLE_RECIPE",
                "updateRecipesMissingIngredients: updated count $count"
            )
            Log.d(
                "SINGLE_RECIPE",
                "updateRecipesMissingIngredients: recipe $recipe"
            )

            // Update the recipe's number of missing ingredients
            recipe.recipe.numMissingIngredients = count
            parentRecipeViewModel.update(recipe.recipe)
        }
    }
}