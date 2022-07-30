package com.ala158.magicpantry.ui.singlerecipe

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.RecipeIngredientArrayAdapter
import com.ala158.magicpantry.arrayAdapter.RecipeListArrayAdapter
import com.ala158.magicpantry.data.RecipeWithIngredients
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.RecipeViewModel

class SingleRecipeActivity : AppCompatActivity() {

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var ingredientViewModel: IngredientViewModel
    private lateinit var recipeName:TextView
    private lateinit var recipeDescription:TextView
    private lateinit var ingredientListView: ListView
    private lateinit var cookingTimeView: TextView
    private lateinit var numOfServingsView:TextView
    private lateinit var isAbleToCookImageView: ImageView
    private lateinit var isAbleToCookTextView: TextView
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
        ingredientViewModel = Util.createViewModel(
            this,
            IngredientViewModel::class.java,
            Util.DataType.INGREDIENT
        )
        recipeName = findViewById(R.id.singleRecipeName)
        recipeDescription = findViewById(R.id.recipe_description)
        ingredientListView = findViewById(R.id.listview_all_recipe_ingredients)
        cookingTimeView = findViewById(R.id.CookingTime)
        numOfServingsView =findViewById(R.id.NoOfServings)
        isAbleToCookImageView = findViewById(R.id.enoughIngredientsImage)
        isAbleToCookTextView = findViewById(R.id.EnoughIngredientsText)
        editRecipeButton = findViewById(R.id.edit_recipe_button)
        addIngredientsButton = findViewById(R.id.add_ingredient_to_recipe_button)

        val id = intent.getIntExtra("RECIPE_KEY",-1)
        val id2 = intent.getIntExtra("RECIPE_KEY_COOKABLE",-1)
        recipeIngredientArrayAdapter = RecipeIngredientArrayAdapter(this, ArrayList(), ArrayList())
        ingredientListView.adapter = recipeIngredientArrayAdapter

        recipeViewModel.allRecipes.observe(this) {
            recipeViewModel.updateCurrentCookable()
            if(id != -1) {
                var recipeWithIngredients: RecipeWithIngredients = it[id]
                recipeName.text = recipeWithIngredients.recipe.title
                recipeDescription.text = recipeWithIngredients.recipe.description
                cookingTimeView.text = "${recipeWithIngredients.recipe.timeToCook}"
                numOfServingsView.text = "${recipeWithIngredients.recipe.servings}"
                if(recipeWithIngredients.recipe.numMissingIngredients == 0){
                    isAbleToCookImageView.setImageResource(R.drawable.ic_baseline_check_box_24)
                    isAbleToCookTextView.text = "Ready to cook!"
                }
                else{
                    isAbleToCookImageView.setImageResource(R.drawable.low_stock)
                    isAbleToCookTextView.text = "Missing ${recipeWithIngredients.recipe.numMissingIngredients} ingredients"
                }
                recipeIngredientArrayAdapter.replaceRecipeIngredients(it[id].ingredients)
                recipeIngredientArrayAdapter.notifyDataSetChanged()
            }
        }
        recipeViewModel.cookableRecipes.observe(this){
            if(id2 != -1) {
                var recipeWithIngredients: RecipeWithIngredients = it[id2]
                recipeName.text = recipeWithIngredients.recipe.title
                cookingTimeView.text = "${recipeWithIngredients.recipe.timeToCook}"
                numOfServingsView.text = "${recipeWithIngredients.recipe.servings}"
                if(recipeWithIngredients.recipe.numMissingIngredients == 0){
                    isAbleToCookImageView.setImageResource(R.drawable.ic_baseline_check_box_24)
                    isAbleToCookTextView.text = "Ready to cook!"
                }
                else{
                    isAbleToCookImageView.setImageResource(R.drawable.low_stock)
                    isAbleToCookTextView.text = "Missing ${recipeWithIngredients.recipe.numMissingIngredients} ingredients"
                }
                recipeIngredientArrayAdapter.replaceRecipeIngredients(it[id2].ingredients)
                recipeIngredientArrayAdapter.notifyDataSetChanged()
            }
        }

        ingredientViewModel.allIngredientsLiveData.observe(this){
            recipeIngredientArrayAdapter.replaceAllIngredients(it)
            recipeIngredientArrayAdapter.notifyDataSetChanged()
        }

        editRecipeButton.setOnClickListener{

        }

        addIngredientsButton.setOnClickListener{

        }
    }
}