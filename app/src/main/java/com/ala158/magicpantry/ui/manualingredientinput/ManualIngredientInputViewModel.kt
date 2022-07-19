package com.ala158.magicpantry.ui.manualingredientinput

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ala158.magicpantry.IngredientEntry
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.repository.MagicPantryRepository

class ManualIngredientInputViewModel(private val repository: MagicPantryRepository) : ViewModel() {
    val ingredient = MutableLiveData<IngredientEntry>()

    init {
        ingredient.value = IngredientEntry()
    }

    fun insertIngredient() {
        val pricePerUnit = ingredient.value!!.getPrice()/ingredient.value!!.getAmount()
        var price = ingredient.value!!.getPrice()
        if (ingredient.value!!.getAmount() > 0) {
            price = pricePerUnit
        }
        val dbIngredient = Ingredient(
            ingredient.value!!.getName(),
            ingredient.value!!.getAmount(),
            ingredient.value!!.getUnit(),
            price
        )
        repository.insertIngredient(dbIngredient)
    }
}