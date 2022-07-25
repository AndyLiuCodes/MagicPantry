package com.ala158.magicpantry.ui.manualingredientinput

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ala158.magicpantry.IngredientEntry
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.repository.MagicPantryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class ManualIngredientInputViewModel(private val repository: MagicPantryRepository) : ViewModel() {
    val ingredient = MutableLiveData<IngredientEntry>()

    init {
        ingredient.value = IngredientEntry()
    }

    fun insertIngredient() {
        // Check if an existing ingredient exists, if so, add to the amount and update the price
        CoroutineScope(Dispatchers.IO).launch {
            val existingtDbEntry = repository.getIngredientByNameAndUnit(
                ingredient.value!!.getName(),
                ingredient.value!!.getUnit()
            )

            if (existingtDbEntry != null) {
                val dbIngredient = Ingredient(
                    ingredient.value!!.getName(),
                    ingredient.value!!.getAmount() + existingtDbEntry.amount,
                    ingredient.value!!.getUnit(),
                    ingredient.value!!.getPrice() // Update the price with the newly entered in one
                )
                dbIngredient.id = existingtDbEntry.id
                repository.updateIngredient(dbIngredient)
            } else {
                // If not existing item exists, then add the ingredient to the db
                val dbIngredient = Ingredient(
                    ingredient.value!!.getName(),
                    ingredient.value!!.getAmount(),
                    ingredient.value!!.getUnit(),
                    ingredient.value!!.getPrice()
                )
                repository.insertIngredient(dbIngredient)
            }
        }
    }
}