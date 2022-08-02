package com.ala158.magicpantry.arrayAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.RecipeWithRecipeItems
import com.ala158.magicpantry.viewModel.RecipeViewModel

class AddRecipeArrayAdapter(
    private val context: Context,
    private var recipes: Array<RecipeWithRecipeItems>,
    recipeViewModel: RecipeViewModel
) : BaseAdapter() {

    val viewModel = recipeViewModel

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
        val view: View = View.inflate(context, R.layout.list_item_recipe_ingredient_list, null)

        val price = view.findViewById<TextView>(R.id.recipe_ingredient_list_item_amount)
        val unit = view.findViewById<TextView>(R.id.recipe_ingredient_list_item_unit)
        val name = view.findViewById<TextView>(R.id.recipe_ingredient_list_item_name)

        val deleteBtn = view.findViewById<Button>(R.id.recipe_ingredient_list_item_delete_button)

        deleteBtn.setOnClickListener {
            viewModel.delete(recipes[position].recipe)
        }

        val recipe = recipes[position]

        price.text = recipe.recipeItems[position].ingredient.price.toString()
        unit.text = recipe.recipeItems[position].ingredient.unit
        name.text = recipe.recipeItems[position].ingredient.name

        /*price.text = "4"
        unit.text = "kg"
        name.text = "bread"*/

        return view
    }
}