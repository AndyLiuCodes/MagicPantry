package com.ala158.magicpantry.ui.recipes

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.AddIngredientToRecipeArrayAdapter
import com.ala158.magicpantry.data.RecipeWithRecipeItems
import com.ala158.magicpantry.viewModel.RecipeViewModel

class AddIngredientToRecipeActivity : AppCompatActivity() {
    private lateinit var ingredients : ListView

    private lateinit var recipeViewModel : RecipeViewModel

    private var recipeIngredientArray = arrayOf<RecipeWithRecipeItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ingredient_to_recipe)

        //start up database
        recipeViewModel = Util.createViewModel(
            this,
            RecipeViewModel::class.java,
            Util.DataType.RECIPE
        )

        ingredients = findViewById(R.id.recipe_add_ingredient_listView)

        //TODO: fetch from db and onclick
        recipeViewModel.allRecipes.observe(this) {
            val myList = it.toTypedArray()

            //if not empty
            if (myList.isNotEmpty()) {
                recipeIngredientArray = myList
            }
        }

        val adapter = AddIngredientToRecipeArrayAdapter(this, recipeIngredientArray)
        ingredients.adapter = adapter

        ingredients.setOnItemClickListener { parent: AdapterView<*>, _: View, _: Int, _: Long->
            println("debug: $parent")
        }

        val saveIngredients = findViewById<Button>(R.id.btn_add_all_ingredients_to_recipe)
        saveIngredients.setOnClickListener {
            //TODO: save ingredients
            updateDatabase()
            onBackPressed()
        }
    }

    //update database
    private fun updateDatabase() {
        /*
        //save to database
        val recipe = Recipe()
        itemViewModel.insert(recipe)*/
    }
}