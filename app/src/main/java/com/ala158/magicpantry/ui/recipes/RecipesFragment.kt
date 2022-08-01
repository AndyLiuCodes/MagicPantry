package com.ala158.magicpantry.ui.recipes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ala158.magicpantry.MockData
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.RecipeListArrayAdapter
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.RecipeItem
import com.ala158.magicpantry.data.RecipeWithRecipeItems
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.RecipeItemViewModel
import com.ala158.magicpantry.viewModel.RecipeViewModel

class RecipesFragment : Fragment(), CompoundButton.OnCheckedChangeListener {
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var ingredientViewModel: IngredientViewModel
    private lateinit var recipeListView: ListView
    private lateinit var addRecipeButton: Button
    private lateinit var recipeListArrayAdapter: RecipeListArrayAdapter
    private lateinit var currentCookableCheckBox: CheckBox
    private lateinit var recipeHeader: TextView

    private lateinit var recipeItemViewModel: RecipeItemViewModel

    private lateinit var ingredients: List<Ingredient>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recipeViewModel = Util.createViewModel(
            requireActivity(),
            RecipeViewModel::class.java,
            Util.DataType.RECIPE
        )
        ingredientViewModel = Util.createViewModel(
            requireActivity(),
            IngredientViewModel::class.java,
            Util.DataType.INGREDIENT
        )

        recipeItemViewModel = Util.createViewModel(
            requireActivity(),
            RecipeItemViewModel::class.java,
            Util.DataType.RECIPE_ITEM
        )

        val view = inflater.inflate(R.layout.fragment_recipes, container, false)
        recipeListView = view.findViewById(R.id.listview_all_recipe)
        addRecipeButton = view.findViewById(R.id.add_recipe)
        currentCookableCheckBox = view.findViewById(R.id.checkbox_filter_cookable_recipes)
        recipeListArrayAdapter = RecipeListArrayAdapter(requireActivity(), ArrayList())
        recipeListView.adapter = recipeListArrayAdapter
        recipeHeader = view.findViewById(R.id.header_recipe)

        recipeViewModel.allRecipes.observe(viewLifecycleOwner) {
            recipeViewModel.updateCurrentCookable()
            if(!currentCookableCheckBox.isChecked && it.size >= 1) {
                recipeListArrayAdapter.replace(it)
                recipeListArrayAdapter.notifyDataSetChanged()
                Log.d("RECIPES", "onCreateView: recipes ${it[0]}")
                for (item in it[0].recipeItems) {
                    Log.d("RECIPES", "onCreateView: recipesItems ${item}")
                }
                Log.d("RECIPES", "onCreateView: NUM: recipesItems ${it[0].recipeItems.size}")
            }
        }
        recipeViewModel.cookableRecipes.observe(viewLifecycleOwner){
            //If the checkBox is checked it will show the list of recipes available to be cooked
            if(currentCookableCheckBox.isChecked) {
                recipeListArrayAdapter.replace(it)
                recipeListArrayAdapter.notifyDataSetChanged()
            }
        }

        val addMockIngredientsButton = view.findViewById<Button>(R.id.add_mock_ingredients)
        addMockIngredientsButton.setOnClickListener {
            for (ingredient in MockData.shoppingListIngredients) {
                ingredientViewModel.insert(ingredient)
            }
        }

        ingredientViewModel.allIngredientsLiveData.observe(viewLifecycleOwner) {
            //If a recipe gets changed i.e gets added/removed/restocked. It will proceed to update the
            //current cookable list in the viewModel
            ingredients = it
            recipeViewModel.updateCurrentCookable()
        }
        recipeListView.setOnItemClickListener { _, _, _, id ->

        }
        currentCookableCheckBox.setOnCheckedChangeListener(this)

        addRecipeButton.setOnClickListener {
            // TODO: Remove later - add mock data
            recipeViewModel.insert(MockData.recipe)
            val recipeId = recipeViewModel.newRecipeId.value!! + 1

            val breadItem = RecipeItem(
                recipeAmount = 4,
                relatedIngredientId = ingredients[0].ingredientId,
                recipeUnit = "unit"
            )

            val milkItem = RecipeItem(
                recipeAmount = 50,
                relatedIngredientId = ingredients[3].ingredientId,
                recipeUnit = "mL"
            )
            Log.d("ADD ITEM", "onCreateView: adding ingredients to recipe ${ingredients[0]}")
            Log.d("ADD ITEM", "onCreateView: adding recipeItem to recipe ${breadItem}")
            Log.d("ADD ITEM", "onCreateView: adding ingredients to recipe ${ingredients[3]}")
            Log.d("ADD ITEM", "onCreateView: adding recipeItem to recipe ${milkItem}")
            recipeItemViewModel.insert(breadItem, recipeId)
            recipeItemViewModel.insert(milkItem, recipeId)
        }

        return view
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (currentCookableCheckBox.isChecked) {
            recipeListArrayAdapter.replace(recipeViewModel.cookableRecipes.value!!)
            recipeListArrayAdapter.notifyDataSetChanged()
            setHeaderCookableRecipes()
        } else {
            recipeListArrayAdapter.replace(recipeViewModel.allRecipes.value!!)
            recipeListArrayAdapter.notifyDataSetChanged()
            setHeaderAllRecipes()
        }
    }

    private fun setHeaderAllRecipes() {
        recipeHeader.setBackgroundResource(R.drawable.rounded_bg_blue)
        recipeHeader.setText(R.string.add_recipe)
    }

    private fun setHeaderCookableRecipes() {
        recipeHeader.setBackgroundResource(R.drawable.rounded_bg_green)
        recipeHeader.setText(R.string.cookable_recipe)
        recipeHeader.textSize = 18f
    }
}