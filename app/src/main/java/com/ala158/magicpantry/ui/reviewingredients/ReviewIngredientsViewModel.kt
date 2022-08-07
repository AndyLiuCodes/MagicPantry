package com.ala158.magicpantry.ui.reviewingredients

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ala158.magicpantry.IngredientEntry
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.repository.IngredientRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReviewIngredientsViewModel(private val repository: IngredientRepository) : ViewModel() {
    private val _ingredientList: MutableLiveData<List<Ingredient>> = MutableLiveData()
    var ingredientList: MutableLiveData<List<Ingredient>> = _ingredientList

    val ingredient = MutableLiveData<IngredientEntry>()

    init {
        ingredient.value = IngredientEntry()
    }

    fun insertAll() {
        CoroutineScope(Dispatchers.IO).launch {
            for (ingredient in ingredientList.value!!) {
                // Check if that ingredient already exist in the pantry, if so just add the amount to it
                val existingDbEntry = repository.getIngredientByNameAndUnit(
                    ingredient.name,
                    ingredient.unit
                )

                if (existingDbEntry != null) {
                    ingredient.amount += existingDbEntry.amount
                    ingredient.ingredientId = existingDbEntry.ingredientId
                    repository.updateIngredient(ingredient)
                } else {
                    // If existing item does not exists, then add the ingredient to the db
                    repository.insertIngredient(ingredient)
                }
            }
        }
    }

    fun addToIngredientList(ingredient: List<Ingredient>) {
        _ingredientList.value = ingredient
    }

    fun updateIngredient(position: Int, ingredient: Ingredient) {
        if (_ingredientList.value != null) {
            val oldIngredientList = _ingredientList.value!!.toMutableList()
            oldIngredientList[position] = ingredient
            _ingredientList.value = oldIngredientList
        }
    }
}