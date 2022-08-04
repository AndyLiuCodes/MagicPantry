package com.ala158.magicpantry.ui.reviewingredients

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ala158.magicpantry.MockData
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.repository.IngredientRepository

class ReviewIngredientsViewModel(private val repository: IngredientRepository) : ViewModel() {
    private val _ingredientList = MutableLiveData<List<Ingredient>>()
    val ingredientList: LiveData<List<Ingredient>> = _ingredientList

    fun insertAll() {
        for (ingredient in ingredientList.value!!)
            repository.insertIngredient(ingredient)
    }

    fun addToIngredientList(ingredient: List<Ingredient>){
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