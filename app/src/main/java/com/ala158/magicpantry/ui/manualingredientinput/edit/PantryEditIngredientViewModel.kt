package com.ala158.magicpantry.ui.manualingredientinput.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ala158.magicpantry.IngredientEntry
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.repository.IngredientRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PantryEditIngredientViewModel(private val repository: IngredientRepository) : ViewModel() {
    val ingredientEntry = MutableLiveData<IngredientEntry>()

    fun getIngredientEntry(id: Long) {
        CoroutineScope(IO).launch {
            val ingredientDbEntry = repository.getIngredient(id)

            withContext(Main) {
                val ingredient = IngredientEntry()
                ingredient.setName(ingredientDbEntry.name)
                ingredient.setId(id)
                ingredient.setAmount(ingredientDbEntry.amount)
                ingredient.setUnit(ingredientDbEntry.unit)
                ingredient.setPrice(ingredientDbEntry.price)
                ingredient.setNotifyThreshold(ingredientDbEntry.notifyThreshold)
                ingredient.setIsNotify(ingredientDbEntry.isNotify)
                ingredientEntry.value = ingredient
            }
        }
    }

    fun deleteIngredientEntry(id: Long) {
        repository.deleteIngredientById(id)
    }

    fun updateIngredientEntry(id: Long) {
        val dbIngredient = Ingredient(
            id,
            ingredientEntry.value!!.getName(),
            ingredientEntry.value!!.getAmount(),
            ingredientEntry.value!!.getUnit(),
            ingredientEntry.value!!.getPrice(),
            ingredientEntry.value!!.getIsNotify(),
            ingredientEntry.value!!.getNotifyThreshold()
        )
        repository.updateIngredient(dbIngredient)
    }
}