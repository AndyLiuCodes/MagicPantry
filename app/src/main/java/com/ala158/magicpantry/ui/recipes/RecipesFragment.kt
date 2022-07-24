package com.ala158.magicpantry.ui.recipes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ala158.magicpantry.MockData
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.Recipe
import com.ala158.magicpantry.data.RecipeWithIngredients
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.RecipeViewModel

class RecipesFragment : Fragment() {
    private lateinit var recipesViewModel: RecipeViewModel
    private lateinit var ingredientViewModel: IngredientViewModel

    private lateinit var recipes: List<RecipeWithIngredients>
    private lateinit var ingredients: List<Ingredient>

    private lateinit var addIngredientsButton: Button
    private lateinit var addIngredientButton: Button
    private lateinit var deleteIngredientButton: Button
    private lateinit var createRecipeButton: Button
    private lateinit var updateRecipeButton: Button
    private lateinit var deleteRecipeButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recipesViewModel = Util.createViewModel(
            requireActivity(),
            RecipeViewModel::class.java,
            Util.DataType.RECIPE
        )
        ingredientViewModel = Util.createViewModel(
            requireActivity(),
            IngredientViewModel::class.java,
            Util.DataType.INGREDIENT
        )

        val view = inflater.inflate(R.layout.fragment_recipes, container, false)

        addIngredientsButton = view.findViewById(R.id.addIngredients)
        addIngredientButton = view.findViewById(R.id.addIngredientFromRecipe)
        deleteIngredientButton = view.findViewById(R.id.deleteIngrdientFromRecipe)
        createRecipeButton = view.findViewById(R.id.createRecipe)
        updateRecipeButton = view.findViewById(R.id.updateRecipe)
        deleteRecipeButton = view.findViewById(R.id.deleteRecipe)

        val textView: TextView = view.findViewById(R.id.text_recipes)
        recipesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        ingredientViewModel.allIngredientsLiveData.observe(viewLifecycleOwner){
            val ingredientNames = mutableListOf<String>()
            for (ingredient in it) {
                ingredientNames.add(ingredient.name)
            }
            Log.d("Recipe", "onCreateView: Num: ${it.size} Ingredients: $ingredientNames")
            ingredients = it
        }

        recipesViewModel.allRecipes.observe(requireActivity()) {
            Log.d("Recipe", "onCreateView: Num: ${it.size} Recipe List: $it")
            recipes = it
        }

        addIngredientsButton.setOnClickListener {
            Log.d("Recipe", "onCreateView: add ingredients")
            for (ingredient in MockData.allIngredientsToastTest) {
                ingredientViewModel.insert(ingredient)
            }
        }

        createRecipeButton.setOnClickListener {
            Log.d("Recipe", "onCreateView: create")
            recipesViewModel.insert(MockData.recipe)
            // ID's begin at 1 when query by ID
            val recipeId = recipesViewModel.newRecipeId.value!! + 1
            Log.d("Recipe", "onCreateView: $recipeId")
            recipesViewModel.insertCrossRef(recipeId, ingredients[0].ingredientId)
            recipesViewModel.insertCrossRef(recipeId, ingredients[1].ingredientId)
        }

        updateRecipeButton.setOnClickListener {
            val recipe = recipes[0].recipe
            Log.d("Recipe", "onCreateView: Update $recipe")
            recipe.description = "New Description"
            recipesViewModel.update(recipe)
        }

        addIngredientButton.setOnClickListener {
            Log.d("Recipe", "onCreateView: add ingredient to recipe")
            val id = recipes[0].recipe.recipeId
            recipesViewModel.insertCrossRef(id, ingredients[2].ingredientId)
        }

        deleteIngredientButton.setOnClickListener {
            Log.d("Recipe", "onCreateView: delete ingredient from recipe")
            val id = recipes[0].recipe.recipeId
            recipesViewModel.deleteCrossRef(id, ingredients[2].ingredientId)
        }

        deleteRecipeButton.setOnClickListener {
            Log.d("Recipe", "onCreateView: delete")
            val id = recipes[0].recipe.recipeId
            recipesViewModel.deleteById(id)
        }

        return view
    }
}