package com.ala158.magicpantry.arrayAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.RecipeWithIngredients

class RecipeListArrayAdapter(private val context: Context, private var recipes:List<RecipeWithIngredients>):BaseAdapter() {
    override fun getCount(): Int {
        return recipes.size
    }

    override fun getItem(position: Int): Any {
        return recipes[position]
    }

    override fun getItemId(position: Int): Long {
        return recipes[position].recipe.recipeId
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.list_item_recipe, null)
        val recipeImageView = view.findViewById<ImageView>(R.id.recipeImage)
        val recipeTitleView = view.findViewById<TextView>(R.id.recipeName)
        val cookingTimeView = view.findViewById<TextView>(R.id.CookingTime)
        val numOfServingsView = view.findViewById<TextView>(R.id.NoOfServings)
        val enoughIngredientsImageView = view.findViewById<ImageView>(R.id.enoughIngredientsImage)
        val enoughIngredientsView = view.findViewById<TextView>(R.id.EnoughIngredientsText)

        val currRecipe = recipes[position]
        recipeTitleView.text = "${currRecipe.recipe.title}"
        cookingTimeView.text = "${currRecipe.recipe.timeToCook}"
        numOfServingsView.text ="${currRecipe.recipe.servings} servings"
        if(currRecipe.recipe.numMissingIngredients == 0){
            enoughIngredientsImageView.setImageResource(R.drawable.ic_baseline_add_24)
            enoughIngredientsView.text = "Ready to cook!"
        }
        else{
            enoughIngredientsView.text = "Missing ${currRecipe.recipe.numMissingIngredients} ingredients"
        }
        return view
    }

    fun replace(newRecipes:List<RecipeWithIngredients>){
        recipes = newRecipes.toList()
    }
}