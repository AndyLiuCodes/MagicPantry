package com.ala158.magicpantry.arrayAdapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.ShoppingListItem
import com.ala158.magicpantry.data.ShoppingListItemAndIngredient

class IngredientListAddAdapter(
    private val context: Context,
) : BaseAdapter() {

    private var ingredients: List<Ingredient> = ArrayList()
    private var shoppingListItems: List<ShoppingListItemAndIngredient> = ArrayList()
    private var ingredientsNotInShoppingList: List<Ingredient> = ArrayList()
    // Holds the IDs of which ingredients to be added to the shopping list
    var ingredientsAddToShoppingList: MutableSet<Long> = mutableSetOf()

    override fun getCount(): Int {
        return ingredientsNotInShoppingList.size
    }

    override fun getItem(position: Int): Any {
        return ingredientsNotInShoppingList[position]
    }

    override fun getItemId(position: Int): Long {
        return ingredientsNotInShoppingList[position].ingredientId
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.list_item_ingredient_add, null)
        val nameTextView = view.findViewById<TextView>(R.id.ingredient_list_item_name)
        val inListCheckBox = view.findViewById<CheckBox>(R.id.ingredient_list_is_in_list)

        val ingredient = ingredientsNotInShoppingList[position]

        nameTextView.text = ingredient.name

        inListCheckBox.setOnCheckedChangeListener() { _, isChecked ->
            if (isChecked) {
                ingredientsAddToShoppingList.add(ingredient.ingredientId)
            } else {
                ingredientsAddToShoppingList.remove(ingredient.ingredientId)
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

    // Retrieve ingredients that aren't in the shopping list
    private fun updateIngredientsNotInShoppingList() {
        val shoppingListIngredientIds =
            shoppingListItems.map { it.shoppingListItem.relatedIngredientId }.toSet()

        ingredientsNotInShoppingList =
            ingredients.filter { it.ingredientId !in shoppingListIngredientIds }

        notifyDataSetChanged()
    }
}
