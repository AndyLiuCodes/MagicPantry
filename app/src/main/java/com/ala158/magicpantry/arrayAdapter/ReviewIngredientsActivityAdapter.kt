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
import com.ala158.magicpantry.ui.reviewingredients.ReviewIngredientsActivity
import java.text.DecimalFormat

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
        val nameView = view.findViewById<TextView>(R.id.ingredient_name)
        val amountView = view.findViewById<TextView>(R.id.ingredient_amount)
        val priceUnitView = view.findViewById<TextView>(R.id.ingredient_price_unit)
        val editButton = view.findViewById<Button>(R.id.ingredient_edit_button)
        val numberFormatter = DecimalFormat("#.##")

        val ingredient = ingredients[position]

        editButton.setOnClickListener {
            Log.d("REVIEW EDIT", "getView: edit button $position")
            val bundle = bundleOf(ReviewIngredientsEditActivity.BUNDLE_POSITION_KEY to position)

            val intent = Intent(context, ReviewIngredientsEditActivity::class.java)
            intent.putExtra(ReviewIngredientsEditActivity.BUNDLE_POS_KEY, bundle)
            intent.putExtra(ReviewIngredientsEditActivity.NAME_KEY, ingredient.name)
            intent.putExtra(ReviewIngredientsEditActivity.AMOUNT_KEY, ingredient.amount)
            intent.putExtra(ReviewIngredientsEditActivity.PRICE_KEY, ingredient.price)
            intent.putExtra(ReviewIngredientsEditActivity.UNIT_KEY, ingredient.unit)

            context.startActivity(intent)
        }

        var unit = "x"
        if (ingredient.unit != "unit") {
            unit = ingredient.unit
        }

        val amountString = numberFormatter.format(ingredient.amount)
        amountView.text = "$amountString $unit"

        nameView.text = ingredient.name

        val priceString = numberFormatter.format(ingredient.price)
        priceUnitView.text = "$$priceString/${ingredient.unit}"

        Log.d("myVal", "${ingredient.amount}x ${ingredient.name}")
        return view
    }
}