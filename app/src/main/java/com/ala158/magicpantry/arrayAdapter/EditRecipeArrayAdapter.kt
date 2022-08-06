package com.ala158.magicpantry.arrayAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.RecipeItem
import com.ala158.magicpantry.data.RecipeItemAndIngredient

class EditRecipeArrayAdapter(
    private val context: Context,
    private var recipeItemAndIngredients: ArrayList<RecipeItemAndIngredient>,
    internal val onRecipeEditAmountChangeClickListener: OnRecipeEditAmountChangeClickListener
) : BaseAdapter() {

    interface OnRecipeEditAmountChangeClickListener {
        fun onRecipeEditAmountChangeClick(recipeItem: RecipeItem)
    }

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

        val recipeItemAndIngredient = recipeItemAndIngredients[position]

        deleteBtn.setOnClickListener {
            println("Delete $name")
        }

        amount.setOnClickListener {
            onRecipeEditAmountChangeClickListener
                .onRecipeEditAmountChangeClick(recipeItemAndIngredient.recipeItem)
        }

        amount.text = recipeItemAndIngredient.recipeItem.recipeAmount.toBigDecimal().toString()
        var unitString = "x"
        if (recipeItemAndIngredient.recipeItem.recipeUnit != "unit") {
            unitString = recipeItemAndIngredient.recipeItem.recipeUnit
        }
        unit.text = unitString
        name.text = recipeItemAndIngredient.ingredient.name

        return view
    }

    fun replaceRecipeIngredients(newRecipes: List<RecipeItemAndIngredient>){
        recipeItemAndIngredients = newRecipes as ArrayList<RecipeItemAndIngredient>
    }

}