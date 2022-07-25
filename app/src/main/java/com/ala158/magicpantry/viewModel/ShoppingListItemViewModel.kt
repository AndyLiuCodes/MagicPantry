package com.ala158.magicpantry.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ala158.magicpantry.data.ShoppingListItemAndIngredient
import com.ala158.magicpantry.data.ShoppingListItem
import com.ala158.magicpantry.repository.ShoppingListItemRepository

class ShoppingListItemViewModel(private val repository: ShoppingListItemRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Shopping List Fragment"
    }
    val text: LiveData<String> = _text

    val allShoppingListItemsLiveData: LiveData<List<ShoppingListItemAndIngredient>> =
        repository.allShoppingListItems.asLiveData()

    fun insert(shoppingListItem: ShoppingListItem) {
        repository.insertShoppingListItem(shoppingListItem)
    }

    fun deleteById(key: Long) {
        repository.deleteShoppingListItemById(key)
    }

    fun update(shoppingListItem: ShoppingListItem) {
        repository.updateShoppingListItem(shoppingListItem)
    }
}

