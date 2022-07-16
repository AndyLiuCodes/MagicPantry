package com.ala158.magicpantry.ui.reviewingredients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import com.ala158.magicpantry.MockData
import com.ala158.magicpantry.R
import com.ala158.magicpantry.arrayAdapter.IngredientsArrayAdapter
import com.ala158.magicpantry.dao.IngredientDAO
import com.ala158.magicpantry.database.MagicPantryDatabase
import com.ala158.magicpantry.repository.MagicPantryRepository
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.ViewModelFactory

class ReviewIngredientsFragment : Fragment() {

    private lateinit var ingredientListView: ListView
    private lateinit var database: MagicPantryDatabase
    private lateinit var ingredientDAO: IngredientDAO
    private lateinit var repository: MagicPantryRepository
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var ingredientViewModel: IngredientViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = MagicPantryDatabase.getInstance(requireActivity())
        ingredientDAO = database.ingredientDAO
        repository = MagicPantryRepository(ingredientDAO)
        viewModelFactory = ViewModelFactory(repository)
        ingredientViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(IngredientViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_review_ingredients, container, false)

        ingredientListView = view.findViewById(R.id.reviewIngredientsList)
        ingredientListView.adapter = IngredientsArrayAdapter(requireActivity(), MockData.ingredients)

        return view
    }

}