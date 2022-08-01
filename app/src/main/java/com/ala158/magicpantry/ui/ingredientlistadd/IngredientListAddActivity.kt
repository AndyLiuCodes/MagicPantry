package com.ala158.magicpantry.ui.ingredientlistadd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.IngredientListAddAdapter
import com.ala158.magicpantry.data.ShoppingListItem
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.ShoppingListItemViewModel

class IngredientListAddActivity : AppCompatActivity() {
    private lateinit var ingredientViewModel: IngredientViewModel
    private lateinit var shoppingListItemViewModel: ShoppingListItemViewModel

    private lateinit var header: TextView
    private lateinit var ingredientListView: ListView
    private lateinit var ingredientListAddAdapter: IngredientListAddAdapter
    private lateinit var finishIngredientListAddButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient_list_add)

        ingredientViewModel =
            Util.createViewModel(this, IngredientViewModel::class.java, Util.DataType.INGREDIENT)

        shoppingListItemViewModel =
            Util.createViewModel(
                this,
                ShoppingListItemViewModel::class.java,
                Util.DataType.SHOPPING_LIST_ITEM
            )

        header = findViewById(R.id.header_ingredient_list_add)
        header.text = "Add Items to Shopping List"

        ingredientListView = findViewById(R.id.list_view_all_ingredients)
        finishIngredientListAddButton = findViewById(R.id.finish_ingredient_list_add)

        ingredientListAddAdapter = IngredientListAddAdapter(this)
        ingredientListView.adapter = ingredientListAddAdapter

        shoppingListItemViewModel.allShoppingListItemsLiveData.observe(this) {
            ingredientListAddAdapter.replaceShoppingListItems(it)
            ingredientListAddAdapter.notifyDataSetChanged()
        }

        ingredientViewModel.allIngredientsLiveData.observe(this) {
            ingredientListAddAdapter.replaceIngredients(it)
            ingredientListAddAdapter.notifyDataSetChanged()
        }

        finishIngredientListAddButton.setOnClickListener {
            val ingredients = ingredientListAddAdapter.ingredientsAddToShoppingList

            var shoppingListItem: ShoppingListItem
            for (ingredient in ingredients) {
                shoppingListItem = ShoppingListItem(itemAmount = 0, relatedIngredientId = ingredient)
                shoppingListItemViewModel.insert(shoppingListItem)
            }
            finish()
        }
    }
}