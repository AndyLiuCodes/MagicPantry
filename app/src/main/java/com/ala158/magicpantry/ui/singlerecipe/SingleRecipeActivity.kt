package com.ala158.magicpantry.ui.singlerecipe

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.RecipeIngredientArrayAdapter
import com.ala158.magicpantry.data.RecipeWithRecipeItems
import com.ala158.magicpantry.ui.recipes.EditRecipeActivity
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.RecipeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SingleRecipeActivity : AppCompatActivity() {

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var ingredientViewModel: IngredientViewModel
    private lateinit var recipeName:TextView
    private lateinit var recipeImage:ImageView
    private lateinit var recipeDescription:TextView
    private lateinit var ingredientListView: ListView
    private lateinit var cookingTimeView: TextView
    private lateinit var numOfServingsView: TextView
    private lateinit var isAbleToCookImageView: ImageView
    private lateinit var isAbleToCookTextView: TextView
    private lateinit var editRecipeButton: Button
    private lateinit var addIngredientsButton: Button
    private lateinit var cookNowButton: Button
    private lateinit var recipeIngredientArrayAdapter: RecipeIngredientArrayAdapter

    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var recipeWithRecipeItems: RecipeWithRecipeItems

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_recipe)

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
        recipeImage = findViewById(R.id.singleRecipeImage)
        recipeDescription = findViewById(R.id.recipe_description)
        ingredientListView = findViewById(R.id.listview_all_recipe_ingredients)
        cookingTimeView = findViewById(R.id.CookingTime)
        numOfServingsView = findViewById(R.id.NoOfServings)
        isAbleToCookImageView = findViewById(R.id.enoughIngredientsImage)
        isAbleToCookTextView = findViewById(R.id.EnoughIngredientsText)
        editRecipeButton = findViewById(R.id.edit_recipe_button)
        addIngredientsButton = findViewById(R.id.add_ingredient_to_recipe_button)

        val id = intent.getIntExtra("RECIPE_KEY", -1)
        val id2 = intent.getIntExtra("RECIPE_KEY_COOKABLE", -1)
        recipeIngredientArrayAdapter = RecipeIngredientArrayAdapter(this, ArrayList())
        ingredientListView.adapter = recipeIngredientArrayAdapter

        recipeViewModel.allRecipes.observe(this) {
            recipeViewModel.updateCurrentCookable()
            if(id != -1) {
                recipeWithRecipeItems = it[id]
                recipeName.text = recipeWithIngredients.recipe.title
                if (recipeWithIngredients.recipe.imageUri == "") {
                    recipeImage.setImageResource(R.drawable.magic_pantry_app_logo)
                }
                else {
                    recipeImage.setImageURI(recipeWithIngredients.recipe.imageUri.toUri())
                }
                recipeDescription.text = recipeWithIngredients.recipe.description
                cookingTimeView.text = "${recipeWithIngredients.recipe.timeToCook} mins"
                numOfServingsView.text = "${recipeWithIngredients.recipe.servings} servings"
                if(recipeWithIngredients.recipe.numMissingIngredients == 0){
                    isAbleToCookImageView.setImageResource(R.drawable.ic_baseline_check_box_24)
                    isAbleToCookTextView.text = "Ready to cook!"
                    cookNowButton.visibility = View.VISIBLE
                } else {
                    isAbleToCookImageView.setImageResource(R.drawable.low_stock)
                    isAbleToCookTextView.text =
                        "Missing ${recipeWithRecipeItems.recipe.numMissingIngredients} ingredients"
                    cookNowButton.visibility = View.INVISIBLE
                }
                recipeIngredientArrayAdapter.replaceRecipeIngredients(recipeWithRecipeItems.recipeItems)
                recipeIngredientArrayAdapter.notifyDataSetChanged()
            }
        }
        recipeViewModel.cookableRecipes.observe(this) {
            if (id2 != -1) {
                var recipeWithIngredients: RecipeWithRecipeItems = it[id2]
                recipeName.text = recipeWithIngredients.recipe.title
                cookingTimeView.text = "${recipeWithIngredients.recipe.timeToCook} mins"
                numOfServingsView.text = "${recipeWithIngredients.recipe.servings} servings"
                if(recipeWithIngredients.recipe.numMissingIngredients == 0){
                    isAbleToCookImageView.setImageResource(R.drawable.ic_baseline_check_box_24)
                    isAbleToCookTextView.text = "Ready to cook!"
                } else {
                    isAbleToCookImageView.setImageResource(R.drawable.low_stock)
                    isAbleToCookTextView.text =
                        "Missing ${recipeWithIngredients.recipe.numMissingIngredients} ingredients"
                }
                recipeIngredientArrayAdapter.replaceRecipeIngredients(it[id2].recipeItems)
                recipeIngredientArrayAdapter.notifyDataSetChanged()
            }
        }

        ingredientViewModel.allIngredientsLiveData.observe(this){
            //recipeIngredientArrayAdapter.replaceAllIngredients(it)
            recipeIngredientArrayAdapter.notifyDataSetChanged()
        }

        editRecipeButton.setOnClickListener{
            val intent = Intent(this, EditRecipeActivity::class.java)
            intent.putExtra("RecipeChosen", id)
            startActivity(intent)
        }

        addIngredientsButton.setOnClickListener {

        }

        cookNowButton.setOnClickListener {
            Log.d("SINGLE RECIPE", "onCreate: cook recipe!!!")

            if (id != -1) {
                CoroutineScope(Dispatchers.IO).launch {
                    consumeIngredients()
                    // Now update all recipes' num of missing ingredients with the pantry being updated
                    updateRecipesMissingIngredients()
                }
            }

        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                finish()
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction("FINISH")
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    private fun consumeIngredients() {
        // Get all recipeItems
        val recipeItems = recipeWithRecipeItems.recipeItems

        // Update the pantry ingredient amounts
        for (item in recipeItems) {
            // Need to handle unit conversion
            val convertedUnitAmount = Util.unitConversion(
                item.recipeItem.recipeAmount,
                item.ingredient.unit,
                item.recipeItem.recipeUnit
            )
            Log.d(
                "SINGLE_RECIPE",
                "consumeIngredients: Ingredient: ${item.ingredient.name} CURR AMT: " +
                        "${item.ingredient.amount}"
            )
            item.ingredient.amount -= convertedUnitAmount
            Log.d(
                "SINGLE_RECIPE",
                "consumeIngredients: Ingredient: ${item.ingredient.name} New AMT: " +
                        "${item.ingredient.amount}"
            )
            ingredientViewModel.updateSync(item.ingredient)
        }
    }

    private suspend fun updateRecipesMissingIngredients() {
        // List of updated ingredient Ids that have been consumed
        val updatedIngredientsIds =
            recipeWithRecipeItems.recipeItems.map { it.ingredient.ingredientId }

        val recipesWithItemsNotEnough = mutableSetOf<Long>()
        val ingredientsWithRecipeItems =
            ingredientViewModel.findIngredientsWithRecipeItemsById(updatedIngredientsIds)

        for (ingredientWithRecipeItem in ingredientsWithRecipeItems) {

            Log.d(
                "SINGLE_RECIPE",
                "updateRecipesMissingIngredients: ingredient " +
                        ingredientWithRecipeItem.ingredient.name
            )

            // Update all recipe items if the amount is enough to cook with
            for (item in ingredientWithRecipeItem.recipeItems) {
                Log.d(
                    "SINGLE_RECIPE",
                    "updateRecipesMissingIngredients: RecipeItemAMT: ${item.recipeAmount} " +
                            "IngredientAMT: ${ingredientWithRecipeItem.ingredient.amount}"
                )
                val convertedRecipeAmount = Util.unitConversion(
                    item.recipeAmount,
                    ingredientWithRecipeItem.ingredient.unit,
                    item.recipeUnit
                )
                if (convertedRecipeAmount <= ingredientWithRecipeItem.ingredient.amount) {
                    item.recipeIsEnough = true
                } else {
                    item.recipeIsEnough = false
                    recipesWithItemsNotEnough.add(item.relatedRecipeId)
                }
                Log.d(
                    "SINGLE_RECIPE",
                    "updateRecipesMissingIngredients: isEnough: ${item.recipeIsEnough}"
                )
                recipeItemViewModel.updateSync(item)
            }
        }

        // Update all recipes' num of missing ingredients that have had their recipeItems
        // isEnough set to false
        val recipes = recipeViewModel.getRecipesById(recipesWithItemsNotEnough.toList())

        for (recipe in recipes) {
            Log.d(
                "SINGLE_RECIPE",
                "updateRecipesMissingIngredients: recipeItem: ${recipe.recipe.title}"
            )
            var count = 0
            for (recipeItem in recipe.recipeItems) {
                if (!recipeItem.recipeItem.recipeIsEnough) {
                    count += 1
                    Log.d(
                        "SINGLE_RECIPE",
                        "updateRecipesMissingIngredients: ${recipeItem.recipeItem} not enough"
                    )
                }
            }

            Log.d(
                "SINGLE_RECIPE",
                "updateRecipesMissingIngredients: updated count $count"
            )
            Log.d(
                "SINGLE_RECIPE",
                "updateRecipesMissingIngredients: recipe $recipe"
            )

            // Update the recipe's number of missing ingredients
            recipe.recipe.numMissingIngredients = count
            recipeViewModel.update(recipe.recipe)
        }
    }

}