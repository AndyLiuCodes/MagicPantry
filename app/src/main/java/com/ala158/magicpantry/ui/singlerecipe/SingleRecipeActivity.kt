package com.ala158.magicpantry.ui.singlerecipe

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.RecipeIngredientArrayAdapter
import com.ala158.magicpantry.data.RecipeWithIngredients
import com.ala158.magicpantry.viewModel.RecipeViewModel

class SingleRecipeActivity : AppCompatActivity() {

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recipeName:TextView
    private lateinit var description:TextView
    private lateinit var ingredientListView: ListView
    private lateinit var editRecipeButton: Button
    private lateinit var addIngredientsButton: Button
    private lateinit var recipeIngredientArrayAdapter: RecipeIngredientArrayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_single_recipe)
        recipeViewModel = Util.createViewModel(
            this,
            RecipeViewModel::class.java,
            Util.DataType.RECIPE
        )
        recipeName = findViewById(R.id.singleRecipeName)

        val id = intent.getIntExtra("RECIPE_KEY",-1)
        val id2 = intent.getIntExtra("RECIPE_KEY_COOKABLE",-1)


        recipeViewModel.allRecipes.observe(this) {
            recipeViewModel.updateCurrentCookable()
            if(id != -1) {
                var recipeWithIngredients: RecipeWithIngredients = it[id]
                recipeName.text = recipeWithIngredients.recipe.title
            }
        }
        recipeViewModel.cookableRecipes.observe(this){
            if(id2 != -1) {
                var recipeWithIngredients: RecipeWithIngredients = it[id]
                recipeName.text = recipeWithIngredients.recipe.title
            }
        }
    }
}