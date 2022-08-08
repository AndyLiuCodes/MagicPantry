package com.ala158.magicpantry

import com.ala158.magicpantry.data.RecipeItem
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
        var recipeItemsToUpdate: MutableList<RecipeItem>
        val ingredientsWithRecipeItems =
            parentIngredientViewModel.findIngredientsWithRecipeItemsById(updatedIngredientIds)

        for (ingredientWithRecipeItem in ingredientsWithRecipeItems) {
            // Update all recipe items if the amount is enough to cook with
            recipeItemsToUpdate = mutableListOf()
            for (item in ingredientWithRecipeItem.recipeItems) {
                val convertedRecipeAmount = Util.unitConversion(
                    item.recipeAmount,
                    ingredientWithRecipeItem.ingredient.unit,
                    item.recipeUnit
                )
                item.recipeIsEnough =
                    convertedRecipeAmount <= ingredientWithRecipeItem.ingredient.amount
                recipeIdsToUpdate.add(item.relatedRecipeId)
                recipeItemsToUpdate.add(item)
            }
            parentRecipeItemViewModel.updateListSync(recipeItemsToUpdate)
        }
        return recipeIdsToUpdate.toList()
    }

    suspend fun updateRecipesMissingIngredients(
        recipeIdsToUpdate: List<Long>,
        parentRecipeViewModel: RecipeViewModel
    ) {
        // Update all recipes' num of missing ingredients that have had their ingredients updated
        val recipes = parentRecipeViewModel.getRecipesById(recipeIdsToUpdate)

        for (recipe in recipes) {
            var count = 0
            for (recipeItem in recipe.recipeItems) {
                if (!recipeItem.recipeItem.recipeIsEnough) {
                    count += 1
                }
            }
            // Update the recipe's number of missing ingredients
            recipe.recipe.numMissingIngredients = count
            parentRecipeViewModel.update(recipe.recipe)
        }
    }
}