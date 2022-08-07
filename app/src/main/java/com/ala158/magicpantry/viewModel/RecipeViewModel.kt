package com.ala158.magicpantry.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ala158.magicpantry.UpdateDB
import com.ala158.magicpantry.data.*
import com.ala158.magicpantry.repository.RecipeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _newRecipeId = MutableLiveData(0L)
    val newRecipeId: LiveData<Long> = _newRecipeId

    val allRecipes: LiveData<List<RecipeWithRecipeItems>> = repository.allRecipes.asLiveData()
    val cookableRecipes = MutableLiveData<List<RecipeWithRecipeItems>>()

    var toBeAddedToRecipeIngredients: MutableMap<Long, Ingredient> = mutableMapOf()
    var addedRecipeItemAndIngredient = MutableLiveData<ArrayList<RecipeItemAndIngredient>>()
    var idToFilter = MutableLiveData<ArrayList<Int>>()

    // Editing recipe variables
    var originalRecipeData: RecipeWithRecipeItems? = null
    var originalRecipeIngredientIdSet = mutableSetOf<Long>()

    // Stores <unique ingredient id, recipe item>
    var originalRecipeDataToBeDeletedRecipeItems = mutableMapOf<Long, RecipeItem>()

    init {
        addedRecipeItemAndIngredient.value = ArrayList()
    }

    suspend fun getRecipesById(keys: List<Long>): List<RecipeWithRecipeItems> {
        return repository.getRecipesById(keys)
    }

    fun insert(
        recipe: Recipe,
        recipeItemViewModel: RecipeItemViewModel,
        recipeViewModel: RecipeViewModel,
        ingredientViewModel: IngredientViewModel
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = repository.insertRecipe(recipe)
            _newRecipeId.postValue(id)

            for (recipeItemAndIngredientEntry in addedRecipeItemAndIngredient.value!!.listIterator()) {
                recipeItemAndIngredientEntry.recipeItem.relatedRecipeId = id
            }
            val recipeItems = addedRecipeItemAndIngredient.value!!.map { it.recipeItem }
            recipeItemViewModel.insertList(recipeItems)
            val ids = addedRecipeItemAndIngredient.value!!.map { it.ingredient.ingredientId }
            UpdateDB.postUpdatesAfterModifyIngredient(
                ids,
                ingredientViewModel,
                recipeItemViewModel,
                recipeViewModel
            )
        }
    }

    fun updateRecipeItemAmount(recipeItem: RecipeItem, amount: Double) {
        for (recipeItemAndIngredient in addedRecipeItemAndIngredient.value!!) {
            if (recipeItemAndIngredient.recipeItem.relatedIngredientId == recipeItem.relatedIngredientId) {
                recipeItemAndIngredient.recipeItem.recipeAmount = amount
                break
            }
        }
    }

    fun update(recipe: Recipe) {
        repository.updateRecipe(recipe)
    }

    fun updateRecipeWithRecipeItems(
        recipe: Recipe,
        recipeItemViewModel: RecipeItemViewModel,
        recipeViewModel: RecipeViewModel,
        ingredientViewModel: IngredientViewModel
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            // Update Recipe Info
            update(recipe)
            val recipeItemsToAdd = mutableListOf<RecipeItem>()
            val recipeItemsToUpdate = mutableListOf<RecipeItem>()
            val recipeItemsToDelete = originalRecipeDataToBeDeletedRecipeItems.values.toList()

            // Update Ingredient List in recipe
            for (recipeItemAndIngredient in addedRecipeItemAndIngredient.value!!.listIterator()) {
                // If item exists in original set then update it
                if (originalRecipeIngredientIdSet.contains(recipeItemAndIngredient.ingredient.ingredientId) &&
                    !originalRecipeDataToBeDeletedRecipeItems.keys.contains(recipeItemAndIngredient.ingredient.ingredientId)
                ) {
                    recipeItemsToUpdate.add(recipeItemAndIngredient.recipeItem)
/*
                    recipeItemViewModel.update(recipeItemAndIngredient.recipeItem)
*/
                } else {
                    // if item does not exist in original set, then insert it or if the original
                    // was marked for deletion
                    recipeItemAndIngredient.recipeItem.relatedRecipeId = recipe.recipeId
                    recipeItemsToAdd.add(recipeItemAndIngredient.recipeItem)
/*
                    recipeItemViewModel.insert(recipeItemAndIngredient.recipeItem)
*/
                }
            }

            // For deleted items that are in the original set, then delete it from DB
/*
            for (toDeleteRecipeItem in originalRecipeDataToBeDeletedRecipeItems.values) {
                recipeItemViewModel.deleteById(toDeleteRecipeItem.recipeItemId)
            }
*/

            recipeItemViewModel.updateList(recipeItemsToUpdate)
            recipeItemViewModel.insertList(recipeItemsToAdd)
            recipeItemViewModel.deleteList(recipeItemsToDelete)

            val ids = addedRecipeItemAndIngredient.value!!.map { it.ingredient.ingredientId }
            UpdateDB.postUpdatesAfterModifyIngredient(
                ids,
                ingredientViewModel,
                recipeItemViewModel,
                recipeViewModel
            )
        }
    }

    fun delete(recipe: Recipe) {
        repository.deleteRecipe(recipe)
    }

    fun updateCurrentCookable() {
        CoroutineScope(Dispatchers.IO).launch {
            val cookableArrayList = ArrayList<RecipeWithRecipeItems>()
            if (allRecipes.value != null) {
                for (currRecipe in allRecipes.value!!) {
                    if (currRecipe.recipe.numMissingIngredients == 0) {
                        cookableArrayList.add(currRecipe)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                cookableRecipes.value = cookableArrayList
            }
        }
    }

}