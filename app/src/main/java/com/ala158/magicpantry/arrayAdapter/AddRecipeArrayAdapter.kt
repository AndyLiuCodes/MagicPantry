package com.ala158.magicpantry.arrayAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.RecipeItem
import com.ala158.magicpantry.data.RecipeItemAndIngredient

class AddRecipeArrayAdapter(
    private val context: Context,
    private var recipeItemAndIngredients: ArrayList<RecipeItemAndIngredient>,
    internal val onRecipeEditAmountChangeClickListener: OnRecipeEditAmountChangeClickListener,
    internal val onRecipeItemDeleteClickListener: OnRecipeItemDeleteClickListener
) : BaseAdapter() {

    interface OnRecipeEditAmountChangeClickListener {
        fun onRecipeEditAmountChangeClick(recipeItem: RecipeItem)
    }

    interface OnRecipeItemDeleteClickListener {
        fun onRecipeItemDelete(name: String, unit: String)
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

        val recipeItemAndIngredient = recipeItemAndIngredients[position]

        val deleteBtn = view.findViewById<Button>(R.id.recipe_ingredient_list_item_delete_button)

        amount.setOnClickListener {
            onRecipeEditAmountChangeClickListener
                .onRecipeEditAmountChangeClick(recipeItemAndIngredient.recipeItem)
        }

        deleteBtn.setOnClickListener {
            recipeItemAndIngredients.removeAt(position)
            onRecipeItemDeleteClickListener.onRecipeItemDelete(
                recipeItemAndIngredient.ingredient.name,
                recipeItemAndIngredient.recipeItem.recipeUnit
            )
        }

        amount.text = recipeItemAndIngredient.recipeItem.recipeAmount.toBigDecimal().toPlainString()

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