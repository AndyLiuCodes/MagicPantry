package com.ala158.magicpantry.arrayAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.viewModel.ShoppingListItemViewModel

class LowIngredientArrayAdapter(
    private val context: Context,
    private var lowIngredientList: List<Ingredient>,
    private var shoppingListItemViewModel: ShoppingListItemViewModel
) : BaseAdapter() {

    init {
        updateValidIngredients()
    }

    // Get ingredients that have a pantry amount < notify threshold amount
    private var validIngredientIds = mutableSetOf<Long>()
    var validIngredients = mutableListOf<Ingredient>()

    override fun getCount(): Int {
        return lowIngredientList.size
    }

    override fun getItem(position: Int): Any {
        return lowIngredientList[position]
    }

    override fun getItemId(position: Int): Long {
        return lowIngredientList[position].ingredientId
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(context, R.layout.list_item_low_ingredient_list, null)

        val itemName = view.findViewById<TextView>(R.id.low_ingredient_list_item_name)
        val addBtn = view.findViewById<ImageButton>(R.id.low_ingredient_list_item_add_button)

        val ingredient = lowIngredientList[position]
        if (ingredient.ingredientId !in validIngredientIds) {
            addBtn.visibility = View.GONE
        }

        addBtn.setOnClickListener {
            shoppingListItemViewModel.insertLowIngredientsFromNotifications(arrayListOf(ingredient))
            Toast.makeText(context, "Added ${ingredient.name}", Toast.LENGTH_SHORT).show()
        }

        itemName.text = ingredient.name

        return view
    }

    fun replace(newIngredients: List<Ingredient>) {
        lowIngredientList = newIngredients
        updateValidIngredients()
    }

    private fun updateValidIngredients() {
        for (ingredient in lowIngredientList) {
            if (ingredient.amount <= ingredient.notifyThreshold) {
                validIngredientIds.add(ingredient.ingredientId)
                validIngredients.add(ingredient)
            }
        }
        notifyDataSetChanged()
    }
}