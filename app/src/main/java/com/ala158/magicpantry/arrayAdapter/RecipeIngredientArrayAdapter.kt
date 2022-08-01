package com.ala158.magicpantry.arrayAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.RecipeWithIngredients

class RecipeIngredientArrayAdapter(private val context: Context, private var recipeIngredients:List<Ingredient>, private var allIngredients:List<Ingredient>): BaseAdapter() {
    override fun getCount(): Int {
        return recipeIngredients.size
    }

    override fun getItem(position: Int): Any {
        return recipeIngredients[position]
    }

    override fun getItemId(position: Int): Long {
        return recipeIngredients[position].ingredientId
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.list_item_recipe_ingredients, null)
        val ingredientAmount: TextView = view.findViewById<TextView>(R.id.recipe_ingredient_amount)
        val ingredientName: TextView = view.findViewById<TextView>(R.id.recipe_ingredient_name)
        val alertSymbol: ImageView = view.findViewById<ImageView>(R.id.recipeIngredient_low_stock_icon)
        ingredientAmount.text = "${recipeIngredients[position].amount} ${recipeIngredients[position].unit}"
        ingredientName.text = "${recipeIngredients[position].name}"
        val findIngredient = allIngredients.find {it.name.lowercase() == recipeIngredients[position].name.lowercase()}
        if (findIngredient != null) {
            if(findIngredient.amount < recipeIngredients[position].amount){
                alertSymbol.visibility = View.VISIBLE
            }else{
                alertSymbol.visibility = View.INVISIBLE
            }
        }
        else{
            alertSymbol.visibility = View.VISIBLE
        }
        return view
    }

    fun replaceRecipeIngredients(newRecipes:List<Ingredient>){
        recipeIngredients = newRecipes.toList()
    }

    fun replaceAllIngredients(newIngredients:List<Ingredient>){
        allIngredients = newIngredients.toList()
    }
}