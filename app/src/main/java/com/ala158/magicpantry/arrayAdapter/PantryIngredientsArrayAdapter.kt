package com.ala158.magicpantry.arrayAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.Ingredient
import java.text.DecimalFormat

class PantryIngredientsArrayAdapter(
    private val context: Context,
    private var ingredients: List<Ingredient>,
    internal val onItemAddToShoppingClickListener: OnItemAddToShoppingClickListener
) : BaseAdapter() {

    interface OnItemAddToShoppingClickListener {
        fun onItemAddToShoppingListClick(ingredient: Ingredient)
    }

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
        val view = View.inflate(context, R.layout.list_item_pantry_ingredient, null)
        val lowStockIconView = view.findViewById<ImageView>(R.id.pantry_low_stock_icon)
        val amountTextView = view.findViewById<TextView>(R.id.ingredient_amount)
        val ingredientNameTextView = view.findViewById<TextView>(R.id.ingredient_name)
        val ingredientPriceUnitTextView = view.findViewById<TextView>(R.id.ingredient_price_unit)
        val addToShoppingListBtn = view.findViewById<Button>(R.id.ingredient_add_to_shopping_list_button)

        val ingredient = ingredients[position]

        // Get the unit type, if the unit type is "unit", then we show "x",
        // else we show the actual unit label (kg, mL, L, etc.)
        var unit = "x"
        if (ingredient.unit != "unit") {
            unit = ingredient.unit
        }

        amountTextView.text = "${ingredient.amount} $unit"
        ingredientNameTextView.text = ingredient.name

        if (ingredient.price > 0.0) {
            // Had help to format the distance value string to have at most 2 decimal places with
            // no trailing zeros from
            // https://www.java67.com/2014/06/how-to-format-float-or-double-number-java-example.html
            val distanceValueFormatter = DecimalFormat("#.##")
            val distanceString = distanceValueFormatter.format(ingredient.price)
            ingredientPriceUnitTextView.text = "$$distanceString/${ingredient.unit}"
        }

        // TODO: Make this a configurable number - maybe in settings?
        if (ingredient.amount < 3) {
            lowStockIconView.visibility = View.VISIBLE
        }

        addToShoppingListBtn.setOnClickListener {
            onItemAddToShoppingClickListener.onItemAddToShoppingListClick(ingredient)
        }

        return view
    }

    fun replace(newIngredients: List<Ingredient>) {
        ingredients = newIngredients.toList()
    }

}