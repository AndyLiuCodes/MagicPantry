package com.ala158.magicpantry.arrayAdapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.ui.manualingredientinput.ManualIngredientInputActivity
import com.google.android.material.textfield.TextInputEditText
import java.text.DecimalFormat

class PantryIngredientsArrayAdapter(
    private val context: Context,
    private var ingredients: List<Ingredient>,
    private val activity: Activity
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

        // TODO: Check if this is something we want - makes UI cleaner
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
            showDialog(ingredient)
        }

        return view
    }

    fun replace(newIngredients: List<Ingredient>) {
        ingredients = newIngredients.toList()
    }

    private fun showDialog(ingredient: Ingredient) {
        // Open dialog to determine the amount of an item to purchase to the shopping list
        val dialogView = activity.layoutInflater.inflate(
            R.layout.dialog_add_to_shopping_list,
            null
        )
        val unitDropdown = dialogView.findViewById<Spinner>(R.id.add_to_shopping_list_unit_dropdown)
        val unitAdapter = ArrayAdapter.createFromResource(
            activity,
            R.array.unit_items,
            R.layout.spinner_item_unit_dropdown
        )
        unitAdapter.setDropDownViewResource(R.layout.spinner_item_unit_dropdown)
        unitDropdown.adapter = unitAdapter

        // Default to the current unit
        unitDropdown.setSelection(
            ManualIngredientInputActivity.UNIT_DROPDOWN_MAPPING[ingredient.unit]!!
        )

        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle(
            String.format(
                activity.resources.getString(R.string.dialog_add_to_shopping_list_title),
                ingredient.name
            )
        )

        dialogBuilder.setPositiveButton(R.string.dialog_add_button_text) {
            dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                val amountView = dialogView.findViewById<TextInputEditText>(R.id.add_to_shopping_list_amount)

                var amount = 0

                if( amountView.text.toString() != "")
                    amount = amountView.text.toString().toInt()


                if (amount == 0) {
                    Toast.makeText(
                        context,
                        "No items were added to shopping list!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Add item to database
                    val selectedUnit = unitDropdown.selectedItem
                    println("IngredientID: ${ingredient.ingredientId} Amount: $amount, Unit: $selectedUnit")
                }
            }
        }
        dialogBuilder.setNegativeButton(R.string.dialog_cancel_button_text) {
            dialog, which ->
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                dialog.dismiss()
            }
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }
}