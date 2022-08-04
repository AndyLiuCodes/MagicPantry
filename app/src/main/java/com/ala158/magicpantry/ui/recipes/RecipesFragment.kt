package com.ala158.magicpantry.ui.recipes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.ala158.magicpantry.MockData
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.RecipeListArrayAdapter
import com.ala158.magicpantry.data.*
import com.ala158.magicpantry.ui.singlerecipe.SingleRecipeActivity
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.NotificationViewModel
import com.ala158.magicpantry.viewModel.RecipeItemViewModel
import com.ala158.magicpantry.viewModel.RecipeViewModel

class RecipesFragment : Fragment(), CompoundButton.OnCheckedChangeListener {
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var ingredientViewModel: IngredientViewModel
    private lateinit var recipeItemViewModel: RecipeItemViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var recipeListView: ListView
    private lateinit var addRecipeButton: Button
    private lateinit var recipeListArrayAdapter: RecipeListArrayAdapter
    private lateinit var currentCookableCheckBox: CheckBox
    private lateinit var recipeHeader: TextView

    private lateinit var ingredients: List<Ingredient>
    private lateinit var recipes: List<RecipeWithRecipeItems>
    private lateinit var recipeItems: List<RecipeItem>
    private lateinit var notifications: List<NotificationWithIngredients>

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
        notificationViewModel = Util.createViewModel(
            requireActivity(),
            NotificationViewModel::class.java,
            Util.DataType.NOTIFICATION
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
            if (!currentCookableCheckBox.isChecked) {
                recipes = it
                Log.d("MOCK_DATA", "onCreateView: NUM ${recipes.size} recipes $recipes")
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
            Log.d("MOCK_DATA", "onCreateView: NUM: ${ingredients.size} ingredients $ingredients")
        }

        recipeItemViewModel.allRecipeItems.observe(viewLifecycleOwner) {
            recipeItems = it
            Log.d("MOCK_DATA", "onCreateView: NUM: ${recipeItems.size} recipeItems $recipeItems")
        }

        notificationViewModel.allNotifications.observe(viewLifecycleOwner) {
            notifications = it
            Log.d(
                "MOCK_DATA",
                "onCreateView: NUM: ${notifications.size} notifications $notifications"
            )
        }

        recipeListView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(requireActivity(), SingleRecipeActivity::class.java)
            if (currentCookableCheckBox.isChecked) {
                intent.putExtra("RECIPE_KEY_COOKABLE", position)
            } else {
                intent.putExtra("RECIPE_KEY", position)
            }
            startActivity(intent)
        }
        currentCookableCheckBox.setOnCheckedChangeListener(this)

        addRecipeButton.setOnClickListener {
            //Button to add recipe to recipe list
            val intent = Intent(requireContext(), AddRecipeActivity::class.java)
            startActivity(intent)
        }

        val addIngredientsButton = view.findViewById<Button>(R.id.addIngredients)
        val addNotifications = view.findViewById<Button>(R.id.addNotifications)
        val addRecipeItems1 = view.findViewById<Button>(R.id.addRecipeItems1)
        val addRecipe1 = view.findViewById<Button>(R.id.addRecipe1)
        val addRecipeItems2 = view.findViewById<Button>(R.id.addRecipeItems2)
        val addRecipe2 = view.findViewById<Button>(R.id.addRecipe2)
        val deleteRecipesButton = view.findViewById<Button>(R.id.deleteRecipes)
        val deleteIngredientsButton = view.findViewById<Button>(R.id.deleteIngredients)
        val deleteNotificationsButton = view.findViewById<Button>(R.id.deleteNotifications)

        addIngredientsButton.setOnClickListener {
            Log.d("MOCK_DATA", "onCreateView: ADDING INGREDIENTS...")
            ingredientViewModel.insertAll(MockData.mockIngredients)
        }

        addNotifications.setOnClickListener {
            Log.d("MOCK_DATA", "onCreateView: ADDING NOTIFICATIONS...")
            if (ingredients.isNotEmpty()) {
                notificationViewModel.insert(MockData.notifications[0], ingredients.subList(6, 7))
                notificationViewModel.insert(MockData.notifications[1], listOf(ingredients[1]))
            }
        }

        addRecipeItems1.setOnClickListener {
            Log.d("MOCK_DATA", "onCreateView: ADDING RECIPE ITEMS FOR RECIPE 1 TO VIEW MODEL...")
            recipeViewModel.addedRecipeItemAndIngredient.value =
                MockData.generateFrenchToastIngredientList(ingredients.subList(0, 1 + 1))
        }

        addRecipe1.setOnClickListener {
            Log.d("MOCK_DATA", "onCreateView: ADDING RECIPE 1...")
            recipeViewModel.insert(MockData.frenchToastRecipe, recipeItemViewModel)
        }

        addRecipeItems2.setOnClickListener {
            Log.d("MOCK_DATA", "onCreateView: ADDING RECIPE ITEMS FOR RECIPE 2 TO VIEW MODEL...")
            recipeViewModel.addedRecipeItemAndIngredient.value =
                MockData.generateSpaghettiIngredientList(ingredients.subList(2, 4 + 1))
        }

        addRecipe2.setOnClickListener {
            Log.d("MOCK_DATA", "onCreateView: ADDING RECIPE 2...")
            recipeViewModel.insert(MockData.spaghettiRecipe, recipeItemViewModel)
        }

        deleteRecipesButton.setOnClickListener {
            Log.d("MOCK_DATA", "onCreateView: DELETING ALL RECIPES...")
            if (recipes.isNotEmpty()) {
                for (recipeWithRecipeItems in recipes) {
                    recipeViewModel.delete(recipeWithRecipeItems.recipe)
                }
            }
        }

        deleteIngredientsButton.setOnClickListener {
            Log.d("MOCK_DATA", "onCreateView: DELETING ALL INGREDIENTS...")
            if (ingredients.isNotEmpty()) {
                for (ingredient in ingredients) {
                    ingredientViewModel.delete(ingredient)
                }
            }
        }

        deleteNotificationsButton.setOnClickListener {
            Log.d("MOCK_DATA", "onCreateView: DELETING ALL NOTIFICATIONS...")
            if (notifications.isNotEmpty()) {
                for (notificationWithIngredients in notifications) {
                    notificationViewModel.deleteById(notificationWithIngredients.notification.notificationId)
                }
            }
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