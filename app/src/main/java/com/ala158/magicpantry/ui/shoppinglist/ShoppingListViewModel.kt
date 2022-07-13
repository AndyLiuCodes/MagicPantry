package com.ala158.magicpantry.ui.shoppinglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShoppingListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Shopping List Fragment"
    }
    val text: LiveData<String> = _text
}