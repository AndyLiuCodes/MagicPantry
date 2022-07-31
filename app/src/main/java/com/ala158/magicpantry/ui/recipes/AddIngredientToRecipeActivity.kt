package com.ala158.magicpantry.ui.recipes

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ala158.magicpantry.R
import com.ala158.magicpantry.arrayAdapter.AddIngredientToRecipeArrayAdapter
import com.ala158.magicpantry.dao.RecipeDAO
import com.ala158.magicpantry.data.RecipeWithIngredients
import com.ala158.magicpantry.database.MagicPantryDatabase
import com.ala158.magicpantry.repository.RecipeRepository
import com.ala158.magicpantry.viewModel.RecipeViewModel
import com.ala158.magicpantry.viewModel.RecipeViewModelFactory

class AddIngredientToRecipeActivity : AppCompatActivity() {
    private lateinit var ingredients : ListView

    private lateinit var myDataBase : MagicPantryDatabase
    private lateinit var dbDao : RecipeDAO
    private lateinit var repository : RecipeRepository
    private lateinit var viewModelFactory : RecipeViewModelFactory
    private lateinit var itemViewModel : RecipeViewModel

    private var recipeIngredientArray = arrayOf<RecipeWithIngredients>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ingredient_to_recipe)

        //start up database
        myDataBase = MagicPantryDatabase.getInstance(this)
        dbDao = myDataBase.recipeDAO
        repository = RecipeRepository(dbDao)
        viewModelFactory = RecipeViewModelFactory(repository)
        itemViewModel = ViewModelProvider(this, viewModelFactory)[RecipeViewModel::class.java]

        ingredients = findViewById(R.id.recipe_add_ingredient_listView)

        //TODO: fetch from db and onclick
        itemViewModel.allRecipes.observe(this) {
            val myList = it.toTypedArray()

            //if not empty
            if (myList.isNotEmpty()) {
                recipeIngredientArray = myList
            }
        }

        val adapter = AddIngredientToRecipeArrayAdapter(this, recipeIngredientArray)
        ingredients.adapter = adapter

        ingredients.setOnItemClickListener() { parent: AdapterView<*>, _: View, position: Int, _: Long->
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