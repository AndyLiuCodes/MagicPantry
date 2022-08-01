package com.ala158.magicpantry.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.IngredientWithRecipeItems
import com.ala158.magicpantry.repository.IngredientRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IngredientViewModel(private val repository: IngredientRepository) : ViewModel() {
    val allIngredientsLiveData: LiveData<List<Ingredient>> = repository.allIngredients.asLiveData()

    private var _ingredientWithRecipeItems: LiveData<IngredientWithRecipeItems> = MutableLiveData()
    val ingredientWithRecipeItems: LiveData<IngredientWithRecipeItems> = _ingredientWithRecipeItems

    private val _newIngredientId = MutableLiveData(0L)
    val newIngredientId: LiveData<Long> = _newIngredientId

    fun insertReturnId(ingredient: Ingredient) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = repository.insertIngredientReturnId(ingredient)
            _newIngredientId.postValue(id)
        }
    }

    fun findIngredientWithRecipeItemsById(key: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val ingredient = repository.getIngredientWithRecipeItemsById(key).asLiveData()
            _ingredientWithRecipeItems = ingredient
        }
    }

    fun insert(ingredient: Ingredient) {
        repository.insertIngredient(ingredient)
    }

    fun delete(ingredient: Ingredient) {
        repository.deleteIngredient(ingredient)
    }

    fun update(ingredient: Ingredient) {
        repository.updateIngredient(ingredient)
    }
}

