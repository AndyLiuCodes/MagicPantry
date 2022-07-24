package com.ala158.magicpantry.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ala158.magicpantry.data.ShoppingListItemAndIngredient
import com.ala158.magicpantry.data.ShoppingListItem
import com.ala158.magicpantry.repository.ShoppingListItemRepository

class ShoppingListItemViewModel(private val repository: ShoppingListItemRepository) : ViewModel() {
    val allShoppingListItemLiveData: LiveData<List<ShoppingListItem>> =
        repository.allShoppingListItems.asLiveData()

    fun getIngredients(key: Long): LiveData<ShoppingListItemAndIngredient> {
        return repository.getIngredientAndShoppingListItem(key).asLiveData()
    }

    fun insert(shoppingListItem: ShoppingListItem) {
        repository.insertShoppingListItem(shoppingListItem)
    }

    fun delete(shoppingListItem: ShoppingListItem) {
        repository.deleteShoppingListItem(shoppingListItem)
    }

    fun update(shoppingListItem: ShoppingListItem) {
        repository.updateShoppingListItem(shoppingListItem)
    }
}

