package com.ala158.magicpantry.repository

import com.ala158.magicpantry.dao.ShoppingListItemDAO
import com.ala158.magicpantry.data.ShoppingListItemAndIngredient
import com.ala158.magicpantry.data.ShoppingListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ShoppingListItemRepository(private val shoppingListItemDAO: ShoppingListItemDAO) {
    val allShoppingListItems: Flow<List<ShoppingListItemAndIngredient>> =
        shoppingListItemDAO.getAllShoppingListItems()

    fun insertShoppingListItem(shoppingListItem: ShoppingListItem) {
        CoroutineScope(Dispatchers.IO).launch {
            shoppingListItemDAO.insertShoppingListItem(shoppingListItem)
        }
    }

    fun deleteShoppingListItemById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            shoppingListItemDAO.deleteShoppingListItemById(id)
        }
    }

    fun updateShoppingListItem(shoppingListItem: ShoppingListItem) {
        CoroutineScope(Dispatchers.IO).launch {
            shoppingListItemDAO.updateShoppingListItem(shoppingListItem)
        }
    }

    fun insertShoppingListItemFromPantry(shoppingListItem: ShoppingListItem) {
        CoroutineScope(Dispatchers.IO).launch {
            // Check if the existing shoppinglistitem already exist, if so get it
            val existingShoppingListItem = shoppingListItemDAO.getShoppingListItemWithNameAndUnit(
                shoppingListItem.itemName, shoppingListItem.itemUnit
            )

            // TODO: Check workflow logic with team
            if (existingShoppingListItem == null) {
                // If item does not exist then insert as normal
                shoppingListItemDAO.insertShoppingListItem(shoppingListItem)
            } else {
                if (existingShoppingListItem.isItemBought) {
                    // If item is bought, then we set it as un-bought with the amount being
                    // the user entered amount
                    existingShoppingListItem.isItemBought = false
                    existingShoppingListItem.itemAmount = shoppingListItem.itemAmount
                } else {
                    // If item is not bought, then we add the amount to purchase to the existing amount
                    existingShoppingListItem.itemAmount += shoppingListItem.itemAmount
                }
                shoppingListItemDAO.updateShoppingListItem(existingShoppingListItem)
            }
        }
    }
}
