package com.ala158.magicpantry.ui.reviewingredients

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.ala158.magicpantry.MockData
import com.ala158.magicpantry.R
import com.ala158.magicpantry.arrayAdapter.IngredientsArrayAdapter
import com.ala158.magicpantry.dao.IngredientDAO
import com.ala158.magicpantry.database.MagicPantryDatabase
import com.ala158.magicpantry.repository.MagicPantryRepository
import com.ala158.magicpantry.ui.pantry.PantryFragment
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.ViewModelFactory

class ReviewIngredientsFragment : Fragment() {

    private lateinit var ingredientListView: ListView
    private lateinit var database: MagicPantryDatabase
    private lateinit var ingredientDAO: IngredientDAO
    private lateinit var repository: MagicPantryRepository
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var reviewIngredientsViewModel: ReviewIngredientsViewModel

    private lateinit var cancelButton: Button
    private lateinit var addAllButton: Button
    private lateinit var ingredientsArrayAdapter: IngredientsArrayAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_review_ingredients, container, false)

        database = MagicPantryDatabase.getInstance(requireActivity())
        ingredientDAO = database.ingredientDAO
        repository = MagicPantryRepository(ingredientDAO)
        viewModelFactory = ViewModelFactory(repository)
        reviewIngredientsViewModel =
            ViewModelProvider(
                requireActivity(),
                viewModelFactory
            ).get(ReviewIngredientsViewModel::class.java)

        ingredientListView = view.findViewById(R.id.reviewIngredientsList)
        ingredientsArrayAdapter = IngredientsArrayAdapter(requireActivity(), ArrayList())
        ingredientListView.adapter = ingredientsArrayAdapter

        reviewIngredientsViewModel.ingredientList.observe(requireActivity()) {
            ingredientsArrayAdapter.replace(it)
            ingredientsArrayAdapter.notifyDataSetChanged()
        }

        cancelButton = view.findViewById(R.id.reviewCancelButton)
        addAllButton = view.findViewById(R.id.reviewAddAllButton)

        cancelButton.setOnClickListener {
            // TODO When receipt scanning API ready
            // requireActivity().supportFragmentManager.popBackStack()
        }
        addAllButton.setOnClickListener {
            reviewIngredientsViewModel.insertAll()
            Toast.makeText(requireActivity(), "Items added!", Toast.LENGTH_SHORT).show()
            view.findNavController().navigate(R.id.navigation_pantry)
        }

        return view
    }

}