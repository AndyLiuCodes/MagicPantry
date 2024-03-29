package com.ala158.magicpantry.arrayAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.data.RecipeItemAndIngredient
import java.text.DecimalFormat

class RecipeIngredientArrayAdapter(
    private val context: Context,
    private var recipeIngredients: List<RecipeItemAndIngredient>
) : BaseAdapter() {
    override fun getCount(): Int {
        return recipeIngredients.size
    }

    override fun getItem(position: Int): Any {
        return recipeIngredients[position]
    }

    override fun getItemId(position: Int): Long {
        return recipeIngredients[position].recipeItem.recipeItemId
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.list_item_recipe_ingredients, null)
        val ingredientAmount: TextView = view.findViewById<TextView>(R.id.recipe_ingredient_amount)
        val ingredientName: TextView = view.findViewById<TextView>(R.id.recipe_ingredient_name)
        val alertSymbol: ImageView =
            view.findViewById<ImageView>(R.id.recipeIngredient_low_stock_icon)
        val numberFormatter = DecimalFormat("#.##")
        val ingredient = recipeIngredients[position]

        var unitString = "x"
        if (ingredient.recipeItem.recipeUnit != "unit") {
            unitString = ingredient.recipeItem.recipeUnit
        }
        ingredientAmount.text =
            "${numberFormatter.format(ingredient.recipeItem.recipeAmount)} $unitString"
        ingredientName.text = ingredient.ingredient.name

        val convertedUnitAmount = Util.unitConversion(
            ingredient.recipeItem.recipeAmount,
            ingredient.ingredient.unit,
            ingredient.recipeItem.recipeUnit
        )

        if (convertedUnitAmount > ingredient.ingredient.amount) {
            alertSymbol.visibility = View.VISIBLE
        } else {
            alertSymbol.visibility = View.INVISIBLE
        }
        return view
    }

    fun replaceRecipeIngredients(newRecipes: List<RecipeItemAndIngredient>) {
        recipeIngredients = newRecipes.toList()
    }

}