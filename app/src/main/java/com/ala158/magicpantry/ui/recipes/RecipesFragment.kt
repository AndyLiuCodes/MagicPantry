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
    private lateinit var recipes: List<RecipeWithRecipeItems>

    private lateinit var addIngredientsButton: Button
    private lateinit var createRecipeButton: Button
    private lateinit var updateRecipeButton: Button
    private lateinit var updateRecipeItemButton: Button
    private lateinit var deleteRecipeItemButton: Button
    private lateinit var deleteRecipeButton: Button

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

/*        recipeViewModel.allRecipes.observe(viewLifecycleOwner) {
            recipeViewModel.updateCurrentCookable()
            if(!currentCookableCheckBox.isChecked) {
                recipeListArrayAdapter.replace(it)
                recipeListArrayAdapter.notifyDataSetChanged()
            }
        }
        recipeViewModel.cookableRecipes.observe(viewLifecycleOwner){
            //If the checkBox is checked it will show the list of recipes available to be cooked
            if(currentCookableCheckBox.isChecked) {
                recipeListArrayAdapter.replace(it)
                recipeListArrayAdapter.notifyDataSetChanged()
            }
        }*/

        ingredientViewModel.allIngredientsLiveData.observe(viewLifecycleOwner) {
            //If a recipe gets changed i.e gets added/removed/restocked. It will proceed to update the
            //current cookable list in the viewModel
            recipeViewModel.updateCurrentCookable()
        }
        recipeListView.setOnItemClickListener { _, _, _, id ->

        }
        currentCookableCheckBox.setOnCheckedChangeListener(this)

        addRecipeButton.setOnClickListener {
            recipeViewModel.insert(MockData.recipe)
            recipeViewModel.insert(MockData.recipe2)
        }

        ingredientViewModel.allIngredientsLiveData.observe(viewLifecycleOwner) {
            val ingredientNames = mutableListOf<String>()
            for (ingredient in it) {
                ingredientNames.add(ingredient.name)
            }
            Log.d("Recipe", "onCreateView: Num: ${it.size} Ingredients: $ingredientNames")
            ingredients = it
        }

        recipeViewModel.allRecipes.observe(requireActivity()) {
            Log.d("Recipe", "onCreateView: Num: ${it.size} Recipe List: $it")
            recipes = it
        }

        addIngredientsButton = view.findViewById(R.id.addIngredients)
        createRecipeButton = view.findViewById(R.id.addRecipe)
        updateRecipeButton = view.findViewById(R.id.updateRecipe)
        updateRecipeItemButton = view.findViewById(R.id.updateItem)
        deleteRecipeItemButton = view.findViewById(R.id.deleteItem)
        deleteRecipeButton = view.findViewById(R.id.deleteRecipe)

        addIngredientsButton.setOnClickListener {
            Log.d("Recipe", "onCreateView: add ingredients")
            for (ingredient in MockData.allIngredientsToastTest) {
                ingredientViewModel.insert(ingredient)
            }
        }

        createRecipeButton.setOnClickListener {
            Log.d("Recipe", "onCreateView: create")
            recipeViewModel.insert(MockData.recipe)
            // ID's begin at 1 when query by ID
            val recipeId = recipeViewModel.newRecipeId.value!! + 1
            Log.d("Recipe", "onCreateView: $recipeId")

            Log.d("Recipe", "onCreateView: add ingredient to recipe")
            val breadItem = RecipeItem(
                recipeAmount = 4,
                relatedIngredientId = ingredients[0].ingredientId,
                recipeUnit = "unit"
            )

            val milkItem = RecipeItem(
                recipeAmount = 2,
                relatedIngredientId = ingredients[1].ingredientId,
                recipeUnit = "mL"
            )

            recipeItemViewModel.insert(breadItem, recipeId)
            recipeItemViewModel.insert(milkItem, recipeId)
        }

        updateRecipeButton.setOnClickListener {
            val recipe = recipes[0].recipe
            Log.d("Recipe", "onCreateView: Update $recipe")
            recipe.description = "New Description"
            recipeViewModel.update(recipe)
        }

        updateRecipeItemButton.setOnClickListener {
            Log.d("Recipe", "onCreateView: update recipeItem of recipe")
            val recipeItem = recipes[0].recipeItems[0].recipeItem
            recipeItem.recipeAmount = 2
            recipeItemViewModel.update(recipeItem)
        }

        deleteRecipeItemButton.setOnClickListener {
            Log.d("Recipe", "onCreateView: delete recipeItem from recipe")
            val recipeItem = recipes[0].recipeItems[1].recipeItem
            recipeItemViewModel.delete(recipeItem)
        }

        deleteRecipeButton.setOnClickListener {
            Log.d("Recipe", "onCreateView: delete recipe")
            val recipe = recipes[0].recipe
            recipeViewModel.delete(recipe)
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