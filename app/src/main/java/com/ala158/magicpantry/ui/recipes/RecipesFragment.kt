package com.ala158.magicpantry.ui.recipes

import android.os.Bundle
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
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.RecipeViewModel

class RecipesFragment : Fragment(),CompoundButton.OnCheckedChangeListener{
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var ingredientViewModel: IngredientViewModel
    private lateinit var recipeListView: ListView
    private lateinit var addRecipeButton: Button
    private lateinit var recipeListArrayAdapter: RecipeListArrayAdapter
    private lateinit var currentCookableCheckBox: CheckBox
    private lateinit var recipeHeader: TextView

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

        val view = inflater.inflate(R.layout.fragment_recipes, container, false)
        recipeListView = view.findViewById(R.id.listview_all_recipe)
        addRecipeButton = view.findViewById(R.id.add_recipe)
        currentCookableCheckBox = view.findViewById(R.id.checkbox_filter_cookable_recipes)
        recipeListArrayAdapter = RecipeListArrayAdapter(requireActivity(), ArrayList())
        recipeListView.adapter = recipeListArrayAdapter
        recipeHeader = view.findViewById(R.id.header_recipe)

        recipeViewModel.allRecipes.observe(viewLifecycleOwner) {
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
        }

        ingredientViewModel.allIngredientsLiveData.observe(viewLifecycleOwner){
            //If a recipe gets changed i.e gets added/removed/restocked. It will proceed to update the
            //current cookable list in the viewModel
            //recipeViewModel.updateCurrentCookable()
        }

        recipeListView.setOnItemClickListener { _, _, _, id ->

        }
        currentCookableCheckBox.setOnCheckedChangeListener(this)

        addRecipeButton.setOnClickListener{
            recipeViewModel.insert(MockData.recipe)
        }

        return view
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if(currentCookableCheckBox.isChecked){
            recipeViewModel.cookableRecipes.value?.let { recipeListArrayAdapter.replace(it) }
            setHeaderCookableRecipes()
        }
        else{
            recipeViewModel.allRecipes.value?.let { recipeListArrayAdapter.replace(it) }
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
    }
}