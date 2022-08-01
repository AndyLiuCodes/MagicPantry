package com.ala158.magicpantry.ui.recipes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.RecipeListArrayAdapter
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.RecipeItem
import com.ala158.magicpantry.ui.manualingredientinput.edit.PantryEditIngredientActivity
import com.ala158.magicpantry.ui.pantry.PantryFragment
import com.ala158.magicpantry.ui.singlerecipe.SingleRecipeActivity
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
            if (!currentCookableCheckBox.isChecked && it.isNotEmpty()) {
                recipeListArrayAdapter.replace(it)
                recipeListArrayAdapter.notifyDataSetChanged()
            }
        }
        recipeViewModel.cookableRecipes.observe(viewLifecycleOwner) {
            //If the checkBox is checked it will show the list of recipes available to be cooked
            if (currentCookableCheckBox.isChecked) {
                recipeListArrayAdapter.replace(it)
                recipeListArrayAdapter.notifyDataSetChanged()
            }
        }

        ingredientViewModel.allIngredientsLiveData.observe(viewLifecycleOwner) {
            //If a recipe gets changed i.e gets added/removed/restocked. It will proceed to update the
            //current cookable list in the viewModel
            ingredients = it
            recipeViewModel.updateCurrentCookable()
        }

        recipeListView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(requireActivity(), SingleRecipeActivity::class.java)
            if(currentCookableCheckBox.isChecked){
                intent.putExtra("RECIPE_KEY_COOKABLE", position)
            }
            else{
                intent.putExtra("RECIPE_KEY", position)
            }
            startActivity(intent)
        }
        currentCookableCheckBox.setOnCheckedChangeListener(this)

        addRecipeButton.setOnClickListener {
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