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
import com.ala158.magicpantry.data.RecipeWithIngredients
import com.ala158.magicpantry.ui.manualingredientinput.edit.ReviewIngredientsEditActivity

class AddIngredientToRecipeArrayAdapter (
    private val context: Context,
    private var recipes: Array<RecipeWithIngredients>
) : BaseAdapter() {

    override fun getCount(): Int {
        return recipes.size
    }

    override fun getItem(position: Int): Any {
        return recipes[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = View.inflate(context, R.layout.list_item_add_ingredient_to_recipe_list, null)

        val name = view.findViewById<TextView>(R.id.recipe_ingredient_list_item_name)

        val recipe = recipes[position]

        name.text = recipe.ingredients[1].toString()

        return view
    }
}