package com.ala158.magicpantry

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

        for (ingredientWithRecipeItem in ingredientsWithRecipeItems) {
            // Update all recipe items if the amount is enough to cook with
            for (item in ingredientWithRecipeItem.recipeItems) {
                val convertedRecipeAmount = Util.unitConversion(
                    item.recipeAmount,
                    ingredientWithRecipeItem.ingredient.unit,
                    item.recipeUnit
                )
                item.recipeIsEnough = convertedRecipeAmount <= ingredientWithRecipeItem.ingredient.amount
                recipeIdsToUpdate.add(item.relatedRecipeId)
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
                    count += 1
                }
            }
            // Update the recipe's number of missing ingredients
            recipe.recipe.numMissingIngredients = count
            parentRecipeViewModel.update(recipe.recipe)
        }
    }

    fun createNotification(recipe: RecipeWithRecipeItems, notificationViewModel:NotificationViewModel): List<Ingredient>{
        val notification = Notification()
        notification.date = Calendar.getInstance()
        val lowIngredients = mutableListOf<(Ingredient)>()
        val recipeItems = recipe.recipeItems
        for (item in recipeItems) {
          if(item.ingredient.isNotify){
              if(item.ingredient.amount <= item.ingredient.notifyThreshold){
                  lowIngredients.add(item.ingredient)
              }
          }
        }
        if(lowIngredients.size > 0 ){
            if(lowIngredients.size == 1){
                notification.description = "Low On ${lowIngredients[0].name}"
            }
            else{
                notification.description = "Low On ${lowIngredients.size}"
            }
            notificationViewModel.insert(notification,lowIngredients)
        }
        return lowIngredients
    }

    suspend fun createNotificationPantry(ingredientIds: List<Long>, ingredientViewModel: IngredientViewModel, notificationViewModel: NotificationViewModel): List<Ingredient> {
        val notification = Notification()
        notification.date = Calendar.getInstance()
        val ingredientsWithRecipeItems = ingredientViewModel.findIngredientsWithRecipeItemsById(ingredientIds)
        val lowIngredients = mutableListOf<(Ingredient)>()
        for (ingredientWithRecipeItem in ingredientsWithRecipeItems) {
            if(ingredientWithRecipeItem.ingredient.isNotify){
                if(ingredientWithRecipeItem.ingredient.amount <= ingredientWithRecipeItem.ingredient.notifyThreshold){
                    lowIngredients.add(ingredientWithRecipeItem.ingredient)
                }
            }
        }
        if(lowIngredients.size > 0 ){
            if(lowIngredients.size == 1){
                notification.description = "Low On ${lowIngredients[0].name}"
            }
            else{
                notification.description = "Low On ${lowIngredients.size}"
            }
            notificationViewModel.insert(notification,lowIngredients)
        }
        return lowIngredients
    }


}