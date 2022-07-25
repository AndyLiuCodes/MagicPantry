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

}
