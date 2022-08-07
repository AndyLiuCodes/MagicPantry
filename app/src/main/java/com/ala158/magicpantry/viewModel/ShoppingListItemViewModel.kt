package com.ala158.magicpantry.viewModel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.RecipeItemAndIngredient
import com.ala158.magicpantry.data.ShoppingListItemAndIngredient
import com.ala158.magicpantry.data.ShoppingListItem
import com.ala158.magicpantry.repository.ShoppingListItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingListItemViewModel(private val repository: ShoppingListItemRepository) : ViewModel() {

    var toBeAddedToShoppingListItems: MutableMap<Long, Ingredient> = mutableMapOf()

    val allShoppingListItemsLiveData: LiveData<List<ShoppingListItemAndIngredient>> =
        repository.allShoppingListItems.asLiveData()

    fun insert(shoppingListItem: ShoppingListItem) {
        repository.insertShoppingListItem(shoppingListItem)
    }

    fun deleteById(key: Long) {
        repository.deleteShoppingListItemById(key)
    }

    fun deleteAlldeleteAllIsBoughtShoppingListItems() {
        repository.deleteAllIsBoughtShoppingListItems()
    }

    fun update(shoppingListItem: ShoppingListItem) {
        repository.updateShoppingListItem(shoppingListItem)
    }

    suspend fun getShoppingListItemByIngredientId(id: Long): ShoppingListItem? {
        return repository.getShoppingListItemByIngredientId(id)
    }

    fun insertMissingRecipeIngredients(recipeItems: List<RecipeItemAndIngredient>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (item in recipeItems) {
                // If it is not enough then we add it to shopping list
                if (!item.recipeItem.recipeIsEnough) {
                    // Check if there is already an existing shopping list item, if so add the recipe amount to it
                    val existingShoppingListItem = getShoppingListItemByIngredientId(
                        item.recipeItem.relatedIngredientId
                    )

                    if (existingShoppingListItem != null) {
                        // Update the existing shopping list item
                        existingShoppingListItem.itemAmount += item.recipeItem.recipeAmount
                        existingShoppingListItem.isItemBought = false
                        update(existingShoppingListItem)
                    } else {
                        // Add new shopping list item
                        val newShoppingListItem = ShoppingListItem(
                            item.recipeItem.recipeAmount,
                            false,
                            item.recipeItem.relatedIngredientId
                        )
                        insert(newShoppingListItem)
                    }
                }
            }
        }
    }

    fun insertLowIngredientsFromNotifications(ingredients: List<Ingredient>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (ingredient in ingredients) {
                val existingShoppingListItem =
                    getShoppingListItemByIngredientId(ingredient.ingredientId)
                if (existingShoppingListItem == null) {
                    // Need to add it to the shopping list
                    val newShoppingListItem = ShoppingListItem(
                        0.0,
                        false,
                        ingredient.ingredientId
                    )
                    insert(newShoppingListItem)
                }
            }
        }
    }
}

