package com.ala158.magicpantry.ui.reviewingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.ReviewIngredientsArrayAdapter

class ReviewIngredientsFragment : Fragment() {

    private lateinit var ingredientListView: ListView
    private lateinit var reviewIngredientsViewModel: ReviewIngredientsViewModel

    private lateinit var cancelButton: Button
    private lateinit var addAllButton: Button
    private lateinit var reviewIngredientsArrayAdapter: ReviewIngredientsArrayAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_review_ingredients, container, false)

        reviewIngredientsViewModel = Util.createViewModel(
            requireActivity(),
            ReviewIngredientsViewModel::class.java,
            Util.DataType.INGREDIENT
        )

        ingredientListView = view.findViewById(R.id.reviewIngredientsList)
        reviewIngredientsArrayAdapter =
            ReviewIngredientsArrayAdapter(requireActivity(), ArrayList())
        ingredientListView.adapter = reviewIngredientsArrayAdapter

        reviewIngredientsViewModel.ingredientList.observe(requireActivity()) {
            reviewIngredientsArrayAdapter.replace(it)
            reviewIngredientsArrayAdapter.notifyDataSetChanged()
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