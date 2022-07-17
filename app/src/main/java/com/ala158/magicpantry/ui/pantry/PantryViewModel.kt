package com.ala158.magicpantry.ui.pantry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.repository.MagicPantryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PantryViewModel(private val repository: MagicPantryRepository) : ViewModel() {
    val allIngredientsLiveData: LiveData<List<Ingredient>> = repository.allIngredients.asLiveData()
    val lowStockIngredients = MutableLiveData<List<Ingredient>>()

    init {
        lowStockIngredients.value = ArrayList()
    }

    fun insert(ingredient: Ingredient) {
        repository.insertIngredient(ingredient)
    }

    fun delete(ingredient: Ingredient){
        repository.deleteIngredient(ingredient)
    }

    fun update(ingredient: Ingredient){
        repository.updateIngredient(ingredient)
    }

    fun updateLowStockList() {
        CoroutineScope(Dispatchers.IO).launch {
            val lowStockArrayList = ArrayList<Ingredient>()
            for (ingredient in allIngredientsLiveData.value!!) {
                if (ingredient.amount < 3) {
                    lowStockArrayList.add(ingredient)
                }
            }
            withContext(Dispatchers.Main) {
                lowStockIngredients.value = lowStockArrayList
            }
        }
    }
}