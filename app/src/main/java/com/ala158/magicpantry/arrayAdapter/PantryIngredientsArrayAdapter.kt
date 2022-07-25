package com.ala158.magicpantry.arrayAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.Ingredient

class PantryIngredientsArrayAdapter(
    private val context: Context,
    private var ingredients: List<Ingredient>
) : BaseAdapter() {

    override fun getCount(): Int {
        return ingredients.size
    }

    override fun getItem(position: Int): Any {
        return ingredients[position]
    }

    override fun getItemId(position: Int): Long {
        return ingredients[position].ingredientId
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = View.inflate(context, R.layout.list_item_pantry_ingredient, null)
        val nameAmountView = view.findViewById<TextView>(R.id.ingredient_amount)
        val priceUnitView = view.findViewById<TextView>(R.id.ingredient_price_unit)
        val lowStockIconView = view.findViewById<ImageView>(R.id.pantry_low_stock_icon)

        val ingredient = ingredients[position]
        nameAmountView.text = "${ingredient.amount}x ${ingredient.name}"
        val price = String.format("$%.2f", ingredient.price)
        priceUnitView.text = "$price/${ingredient.unit}"

        // TODO: Make this a configurable number - maybe in settings?
        if (ingredient.amount < 3) {
            lowStockIconView.visibility = View.VISIBLE
        }

        return view
    }

    fun replace(newIngredients: List<Ingredient>) {
        ingredients = newIngredients.toList()
    }
}