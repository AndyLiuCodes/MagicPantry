package com.ala158.magicpantry.ui.ingredientlistadd

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.IngredientListAddAdapter
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.RecipeItem
import com.ala158.magicpantry.data.RecipeItemAndIngredient
import com.ala158.magicpantry.data.ShoppingListItem
import com.ala158.magicpantry.dialogs.IngredientListAddDialog
import com.ala158.magicpantry.ui.recipes.AddRecipeActivity.Companion.ADDED_INGREDIENTS_KEY
import com.ala158.magicpantry.ui.recipes.AddRecipeActivity.Companion.IDS_TO_FILTER_KEY
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.RecipeItemViewModel
import com.ala158.magicpantry.viewModel.RecipeViewModel
import com.ala158.magicpantry.viewModel.ShoppingListItemViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IngredientListAddActivity : AppCompatActivity(),
    IngredientListAddDialog.IngredientListAddDialogListener {
    private lateinit var header: TextView
    private lateinit var ingredientListView: ListView
    private lateinit var ingredientListAddAdapter: IngredientListAddAdapter
    private lateinit var finishIngredientListAddButton: Button
    private lateinit var ingredientViewModel: IngredientViewModel
    private lateinit var shoppingListItemViewModel: ShoppingListItemViewModel
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recipeItemViewModel: RecipeItemViewModel

    private var recipeId = 0L
    private var isIngredientAddShoppingList: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient_list_add)

        isIngredientAddShoppingList =
            intent.extras?.getInt(Util.INGREDIENT_ADD_LIST) == Util.INGREDIENT_ADD_SHOPPING_LIST
        ingredientViewModel =
            Util.createViewModel(this, IngredientViewModel::class.java, Util.DataType.INGREDIENT)

        shoppingListItemViewModel =
            Util.createViewModel(
                this,
                ShoppingListItemViewModel::class.java,
                Util.DataType.SHOPPING_LIST_ITEM
            )

        recipeViewModel =
            Util.createViewModel(
                this,
                RecipeViewModel::class.java,
                Util.DataType.RECIPE
            )

        recipeItemViewModel =
            Util.createViewModel(
                this,
                RecipeItemViewModel::class.java,
                Util.DataType.RECIPE_ITEM
            )

        header = findViewById(R.id.header_ingredient_list_add)

        ingredientListView = findViewById(R.id.list_view_all_ingredients)
        finishIngredientListAddButton = findViewById(R.id.finish_ingredient_list_add)

        if (isIngredientAddShoppingList) {
            ingredientListAddAdapter = IngredientListAddAdapter(
                this,
                isIngredientAddShoppingList,
                shoppingListItemViewModel.toBeAddedToShoppingListItems
            )

            header.text = "Add Items to Shopping List"
            shoppingListItemViewModel.allShoppingListItemsLiveData.observe(this) {
                ingredientListAddAdapter.replaceShoppingListItems(it)
                ingredientListAddAdapter.notifyDataSetChanged()
            }
        } else {
            var ingredientsIdToFilter = ArrayList<Int>()
            if (intent.extras != null) {
                val extraData = intent.extras?.getIntegerArrayList(IDS_TO_FILTER_KEY)
                ingredientsIdToFilter =
                    extraData as ArrayList<Int> /* = java.util.ArrayList<kotlin.Int> */
            }
            ingredientListAddAdapter = IngredientListAddAdapter(
                this,
                isIngredientAddShoppingList,
                recipeViewModel.toBeAddedToRecipeIngredients
            )
            recipeViewModel.idToFilter.value = ingredientsIdToFilter
            header.text = "Add Ingredients to Recipe"
            val position = intent.extras?.getInt(Util.INGREDIENT_ADD_LIST_RECIPE_POSITION)!!

            recipeViewModel.idToFilter.observe(this) {
                // If id's to filter has changed update the adapter. Occurs when new items are added/deleted
                ingredientListAddAdapter.replaceRecipeItems(it)
                ingredientListAddAdapter.notifyDataSetChanged()
            }
        }

        ingredientListView.adapter = ingredientListAddAdapter

        ingredientViewModel.allIngredientsLiveData.observe(this) {
            ingredientListAddAdapter.replaceIngredients(it)
            ingredientListAddAdapter.notifyDataSetChanged()
        }

        finishIngredientListAddButton.setOnClickListener {
            val ingredients = ingredientListAddAdapter.ingredientsToAdd
            var shoppingListItem: ShoppingListItem
            var recipeItem: RecipeItem
            var ingredient: Ingredient
            var addedRecipeItemAndIngredient = ArrayList<RecipeItemAndIngredient>()
            for (ingredientMap in ingredients) {
                ingredient = ingredientMap.value
                if (isIngredientAddShoppingList) {
                    shoppingListItem =
                        ShoppingListItem(
                            itemAmount = 0.0,
                            relatedIngredientId = ingredient.ingredientId
                        )
                    shoppingListItemViewModel.insert(shoppingListItem)
                } else {
                    recipeItem = RecipeItem(
                        recipeAmount = 0.0,
                        recipeUnit = ingredient.unit,
                        relatedIngredientId = ingredient.ingredientId,
                        relatedRecipeId = recipeId
                    )
                    val recipeItemAndIngredient = RecipeItemAndIngredient(recipeItem, ingredient)
                    addedRecipeItemAndIngredient.add(recipeItemAndIngredient)
                }
            }

            if (isIngredientAddShoppingList && ingredients.isNotEmpty()) {
                Toast.makeText(this, "Ingredients added to Shopping List!", Toast.LENGTH_SHORT)
                    .show()
            } else if (!isIngredientAddShoppingList && ingredients.isNotEmpty()) {
                Toast.makeText(this, "Ingredients added to Recipe!", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent()
                resultIntent.putExtra(ADDED_INGREDIENTS_KEY, addedRecipeItemAndIngredient)
                setResult(RESULT_OK, resultIntent)
            }

            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (isIngredientAddShoppingList) {
            shoppingListItemViewModel.toBeAddedToShoppingListItems =
                ingredientListAddAdapter.ingredientsToAdd
        } else {
            recipeViewModel.toBeAddedToRecipeIngredients = ingredientListAddAdapter.ingredientsToAdd
        }
    }

    // Used the following resource to create the options menu:
    // https://www.techotopia.com/index.php/Creating_and_Managing_Overflow_Menus_on_Android_with_Kotlin
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ingredient_list_add_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Had help from https://developer.android.com/training/appbar/actions#handle-actions
    // detecting when a user presses on an item on the top menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_menu_ingredientlist_add) {
            val ingredientListAddDialog = IngredientListAddDialog()
            ingredientListAddDialog.show(supportFragmentManager, "Ingredient List Add")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onIngredientListAddDialogClick(
        newIngredientName: String,
        newIngredientUnit: String
    ) {
        val newIngredient = Ingredient(
            newIngredientName,
            0.0,
            newIngredientUnit,
            0.0,
            0.0
        )

        val failedToast = Toast.makeText(
            this,
            "Unable to add ingredient. Ingredient with the specified name and unit already exists.",
            Toast.LENGTH_LONG
        )
        val successToast = Toast.makeText(this, "Ingredient added!", Toast.LENGTH_SHORT)

        CoroutineScope(Dispatchers.IO).launch {
            val existingIngredient =
                ingredientViewModel.getIngredientByNameAndUnit(newIngredientName, newIngredientUnit)
            if (existingIngredient != null) {
                withContext(Dispatchers.Main) {
                    failedToast.show()
                }
            } else {
                ingredientViewModel.insert(newIngredient)
                successToast.show()
            }
        }
    }
}