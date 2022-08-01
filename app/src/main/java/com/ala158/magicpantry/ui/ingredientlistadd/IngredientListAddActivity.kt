package com.ala158.magicpantry.ui.ingredientlistadd

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.IngredientListAddAdapter
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.RecipeItem
import com.ala158.magicpantry.data.ShoppingListItem
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.RecipeItemViewModel
import com.ala158.magicpantry.viewModel.RecipeViewModel
import com.ala158.magicpantry.viewModel.ShoppingListItemViewModel

class IngredientListAddActivity : AppCompatActivity() {
    private lateinit var header: TextView
    private lateinit var ingredientListView: ListView
    private lateinit var ingredientListAddAdapter: IngredientListAddAdapter
    private lateinit var finishIngredientListAddButton: Button

    private var recipeId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient_list_add)

        val isIngredientAddShoppingList =
            intent.extras?.getInt(Util.INGREDIENT_ADD_LIST) == Util.INGREDIENT_ADD_SHOPPING_LIST
        val ingredientViewModel =
            Util.createViewModel(this, IngredientViewModel::class.java, Util.DataType.INGREDIENT)

        val shoppingListItemViewModel =
            Util.createViewModel(
                this,
                ShoppingListItemViewModel::class.java,
                Util.DataType.SHOPPING_LIST_ITEM
            )

        val recipeViewModel =
            Util.createViewModel(
                this,
                RecipeViewModel::class.java,
                Util.DataType.RECIPE
            )

        val recipeItemViewModel =
            Util.createViewModel(
                this,
                RecipeItemViewModel::class.java,
                Util.DataType.RECIPE_ITEM
            )

        header = findViewById(R.id.header_ingredient_list_add)

        ingredientListView = findViewById(R.id.list_view_all_ingredients)
        finishIngredientListAddButton = findViewById(R.id.finish_ingredient_list_add)

        ingredientListAddAdapter = IngredientListAddAdapter(this, isIngredientAddShoppingList)
        ingredientListView.adapter = ingredientListAddAdapter

        ingredientViewModel.allIngredientsLiveData.observe(this) {
            ingredientListAddAdapter.replaceIngredients(it)
            ingredientListAddAdapter.notifyDataSetChanged()
        }

        if (isIngredientAddShoppingList) {
            header.text = "Add Items to Shopping List"
            shoppingListItemViewModel.allShoppingListItemsLiveData.observe(this) {
                ingredientListAddAdapter.replaceShoppingListItems(it)
                ingredientListAddAdapter.notifyDataSetChanged()
            }
        } else {
            header.text = "Add Ingredients to Recipe"
            val position = intent.extras?.getInt(Util.INGREDIENT_ADD_LIST_RECIPE_POSITION)!!
            recipeViewModel.allRecipes.observe(this) {
                ingredientListAddAdapter.replaceRecipeItems(it[position].recipeItems)
                recipeId = it[position].recipe.recipeId
                ingredientListAddAdapter.notifyDataSetChanged()
            }
        }

        finishIngredientListAddButton.setOnClickListener {
            val ingredients = ingredientListAddAdapter.ingredientsToAdd
            var shoppingListItem: ShoppingListItem
            var recipeItem: RecipeItem
            var ingredient: Ingredient
            for (ingredientMap in ingredients) {
                ingredient = ingredientMap.value
                if (isIngredientAddShoppingList) {
                    shoppingListItem =
                        ShoppingListItem(
                            itemAmount = 0,
                            relatedIngredientId = ingredient.ingredientId
                        )
                    shoppingListItemViewModel.insert(shoppingListItem)
                } else {
                    recipeItem = RecipeItem(
                        recipeAmount = 0,
                        recipeUnit = ingredient.unit,
                        relatedIngredientId = ingredient.ingredientId
                    )
                    recipeItemViewModel.insert(recipeItem, recipeId)
                }
            }
            finish()
        }
    }
}