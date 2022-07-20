package com.ala158.magicpantry.arrayAdapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.Ingredient

class ReviewIngredientsActivityAdapter(
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
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = View.inflate(context, R.layout.list_item_edit_ingredient, null)

        val nameAmountView = view.findViewById<TextView>(R.id.ingredientNameAmount)
        val priceUnitView = view.findViewById<TextView>(R.id.ingredientPriceUnit)
        val editButton = view.findViewById<Button>(R.id.ingredientEditButton)

        editButton.setOnClickListener {
            Log.d("REVIEW EDIT", "getView: edit button $position")
//            val bundle = bundleOf("position" to position)
//            view.findNavController().navigate(R.id.navigation_manual_ingredient_input_edit, bundle)
        }

        val ingredient = ingredients[position]
        nameAmountView.text = "${ingredient.amount}x ${ingredient.name}"
        val price = String.format("$%.2f", ingredient.price)
        priceUnitView.text = "$price/${ingredient.unit}"

        Log.d("myVal", "${ingredient.amount}x ${ingredient.name}")
        return view
    }

    fun replace(newIngredients: List<Ingredient>) {
        ingredients = newIngredients.toList()
    }
}