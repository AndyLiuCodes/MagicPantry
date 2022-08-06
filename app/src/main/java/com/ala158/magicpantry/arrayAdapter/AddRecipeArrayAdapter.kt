package com.ala158.magicpantry.arrayAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.RecipeItemAndIngredient
import com.ala158.magicpantry.viewModel.RecipeItemViewModel
import com.ala158.magicpantry.viewModel.RecipeViewModel

class AddRecipeArrayAdapter(
    private val context: Context,
    private var recipeItemAndIngredients: ArrayList<RecipeItemAndIngredient>,
    private val recipeItemViewModel: RecipeItemViewModel
) : BaseAdapter() {

    override fun getCount(): Int {
        return recipeItemAndIngredients.size
    }

    override fun getItem(position: Int): Any {
        return recipeItemAndIngredients[position]
    }

    override fun getItemId(position: Int): Long {
        return recipeItemAndIngredients[position].ingredient.ingredientId
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = View.inflate(context, R.layout.list_item_recipe_ingredient_list, null)

        val amount = view.findViewById<TextView>(R.id.recipe_ingredient_list_item_amount)
        val unit = view.findViewById<TextView>(R.id.recipe_ingredient_list_item_unit)
        val name = view.findViewById<TextView>(R.id.recipe_ingredient_list_item_name)

        val deleteBtn = view.findViewById<Button>(R.id.recipe_ingredient_list_item_delete_button)

        deleteBtn.setOnClickListener {
            recipeItemViewModel.delete(recipeItemAndIngredients[position].recipeItem)
            Toast.makeText(context, "Ingredient deleted", Toast.LENGTH_SHORT).show()
        }

        val recipeItemAndIngredient = recipeItemAndIngredients[position]

        amount.text = recipeItemAndIngredient.recipeItem.recipeAmount.toString()
        unit.text = recipeItemAndIngredient.recipeItem.recipeUnit
        name.text = recipeItemAndIngredient.ingredient.name

        return view
    }

    fun replaceRecipeIngredients(newRecipes: List<RecipeItemAndIngredient>){
        recipeItemAndIngredients = newRecipes as ArrayList<RecipeItemAndIngredient>
    }
}