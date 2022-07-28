package com.ala158.magicpantry.ui.singlerecipe

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.arrayAdapter.RecipeIngredientArrayAdapter

class SingleRecipeActivity : AppCompatActivity() {

    private lateinit var recipeName:TextView
    private lateinit var description:TextView
    private lateinit var ingredientListView: ListView
    private lateinit var editRecipeButton: Button
    private lateinit var addIngredientsButton: Button
    private lateinit var recipeIngredientArrayAdapter: RecipeIngredientArrayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_single_recipe)

    }
}