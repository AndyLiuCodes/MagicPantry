package com.ala158.magicpantry.arrayAdapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.ui.manualingredientinput.edit.ReviewIngredientsEditActivity

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

        val ingredient = ingredients[position]

        editButton.setOnClickListener {
            Log.d("REVIEW EDIT", "getView: edit button $position")
            val bundle = bundleOf("position" to position)

            val intent = Intent(context, ReviewIngredientsEditActivity::class.java)
            intent.putExtra("pos", bundle)
            intent.putExtra("name", ingredient.name)
            intent.putExtra("amount", ingredient.amount)
            intent.putExtra("price", ingredient.price)
            intent.putExtra("unit", ingredient.unit)

            context.startActivity(intent)
        }
        nameAmountView.text = "${ingredient.amount}x ${ingredient.name}"
        val price = String.format("$%.2f", ingredient.price)
        priceUnitView.text = "$price/${ingredient.unit}"

        Log.d("myVal", "${ingredient.amount}x ${ingredient.name}")
        return view
    }
}