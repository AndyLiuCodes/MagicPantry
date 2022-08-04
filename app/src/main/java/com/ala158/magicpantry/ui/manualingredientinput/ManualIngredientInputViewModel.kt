package com.ala158.magicpantry.ui.manualingredientinput

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ala158.magicpantry.IngredientEntry
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.repository.IngredientRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManualIngredientInputViewModel(private val repository: IngredientRepository) : ViewModel() {
    val ingredient = MutableLiveData<IngredientEntry>()

    init {
        ingredient.value = IngredientEntry()
    }

    fun insertIngredient() {
        // Check if an existing ingredient exists, if so, add to the amount and update the price
        CoroutineScope(Dispatchers.IO).launch {
            val existingDbEntry = repository.getIngredientByNameAndUnit(
                ingredient.value!!.getName(),
                ingredient.value!!.getUnit()
            )

            if (existingDbEntry != null) {
                val dbIngredient = Ingredient(
                    ingredient.value!!.getName(),
                    ingredient.value!!.getAmount() + existingDbEntry.amount,
                    ingredient.value!!.getUnit(),
                    ingredient.value!!.getPrice(), // Update the price with the newly entered in one
                    ingredient.value!!.getIsNotify(),
                    ingredient.value!!.getNotifyThreshold()
                )
                dbIngredient.ingredientId = existingDbEntry.ingredientId
                repository.updateIngredient(dbIngredient)
            } else {
                // If not existing item exists, then add the ingredient to the db
                val dbIngredient = Ingredient(
                    ingredient.value!!.getName(),
                    ingredient.value!!.getAmount(),
                    ingredient.value!!.getUnit(),
                    ingredient.value!!.getPrice(),
                    ingredient.value!!.getIsNotify(),
                    ingredient.value!!.getNotifyThreshold()
                )
                repository.insertIngredient(dbIngredient)
            }
        }
    }
}