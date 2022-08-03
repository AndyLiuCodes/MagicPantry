package com.ala158.magicpantry.arrayAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.RecipeItemAndIngredient
import com.ala158.magicpantry.data.ShoppingListItemAndIngredient

class IngredientListAddAdapter(
    private val context: Context,
    private val isIngredientAddShoppingList: Boolean,
    existingIngredientsToAdd: MutableMap<Long, Ingredient>? = null
) : BaseAdapter() {

    private var ingredients: List<Ingredient> = ArrayList()
    private var shoppingListItems: List<ShoppingListItemAndIngredient> = ArrayList()
    private var recipeItems: List<RecipeItemAndIngredient> = ArrayList()
    private var filterIngredientIds = ArrayList<Int>()
    // Holds the IDs of which ingredients to be added to the shopping list or recipe
    var ingredientsToAdd: MutableMap<Long, Ingredient> = mutableMapOf()

    // Only show ingredients that are not in the shopping list or in the recipe
    private var filteredIngredients: List<Ingredient> = ArrayList()

    init {
        if (existingIngredientsToAdd != null) {
            ingredientsToAdd = existingIngredientsToAdd
        }
    }

    override fun getCount(): Int {
        return filteredIngredients.size
    }

    override fun getItem(position: Int): Any {
        return filteredIngredients[position]
    }

    override fun getItemId(position: Int): Long {
        return filteredIngredients[position].ingredientId
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.list_item_ingredient_add, null)
        val nameTextView = view.findViewById<TextView>(R.id.ingredient_list_item_name)
        val inListCheckBox = view.findViewById<CheckBox>(R.id.ingredient_list_is_in_list)

        val ingredient = filteredIngredients[position]

        nameTextView.text = ingredient.name

        if (ingredientsToAdd.containsKey(ingredient.ingredientId)) {
            inListCheckBox.isChecked = true
        }

        inListCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ingredientsToAdd[ingredient.ingredientId] = ingredient
            } else {
                ingredientsToAdd.remove(ingredient.ingredientId)
            }
        }

        return view
    }

    fun replaceIngredients(newIngredients: List<Ingredient>) {
        ingredients = newIngredients
        updateIngredientsNotInShoppingList()
    }

    fun replaceShoppingListItems(newShoppingListItems: List<ShoppingListItemAndIngredient>) {
        shoppingListItems = newShoppingListItems
        updateIngredientsNotInShoppingList()
    }

    fun replaceRecipeItems(newIdsToFilter: ArrayList<Int>) {
        filterIngredientIds = newIdsToFilter
        updateIngredientsNotInShoppingList()
    }

    // Retrieve ingredients that aren't in the shopping list/Recipe
    private fun updateIngredientsNotInShoppingList() {
        val filterIds: Set<Long>
        if (isIngredientAddShoppingList) {
            filterIds =
                shoppingListItems.map { it.shoppingListItem.relatedIngredientId }.toSet()
        } else {
            filterIds = filterIngredientIds.map { it.toLong() }.toSet()
        }

        filteredIngredients =
            ingredients.filter { it.ingredientId !in filterIds }

        notifyDataSetChanged()
    }
}
