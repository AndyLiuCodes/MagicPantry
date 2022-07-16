package com.ala158.magicpantry.ui.manualingredientinput.edit

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.ala158.magicpantry.R
import com.ala158.magicpantry.dao.IngredientDAO
import com.ala158.magicpantry.database.MagicPantryDatabase
import com.ala158.magicpantry.repository.MagicPantryRepository
import com.ala158.magicpantry.ui.reviewingredients.ReviewIngredientsViewModel
import com.ala158.magicpantry.viewModel.ViewModelFactory
import com.google.android.material.textfield.TextInputLayout

class ManualIngredientInputEditFragment : Fragment() {
    private lateinit var nameEditText: TextInputLayout
    private lateinit var amountEditText: TextInputLayout

    // TODO When proper values for unit array is set
    // private lateinit var unit: EditText
    private lateinit var priceEditText: TextInputLayout
    private lateinit var btnAddToPantry: Button
    private lateinit var btnCancel: Button
    private lateinit var manualIngredientInputEditViewModel: ManualIngredientInputEditViewModel

    private lateinit var database: MagicPantryDatabase
    private lateinit var ingredientDAO: IngredientDAO
    private lateinit var repository: MagicPantryRepository
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var reviewIngredientsViewModel: ReviewIngredientsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view =
            inflater.inflate(R.layout.fragment_manual_ingredient_input_edit, container, false)
        btnAddToPantry = view.findViewById(R.id.btn_add_to_pantry)
        btnCancel = view.findViewById(R.id.btn_cancel_pantry)
        nameEditText = view.findViewById(R.id.editName)
        amountEditText = view.findViewById(R.id.editAmount)
        priceEditText = view.findViewById(R.id.editPrice)

        val position = arguments?.getInt("position")

        database = MagicPantryDatabase.getInstance(requireActivity())
        ingredientDAO = database.ingredientDAO
        repository = MagicPantryRepository(ingredientDAO)
        viewModelFactory = ViewModelFactory(repository)
        reviewIngredientsViewModel =
            ViewModelProvider(
                requireActivity(),
                viewModelFactory
            ).get(ReviewIngredientsViewModel::class.java)

        reviewIngredientsViewModel.ingredientList.observe(requireActivity()) {
            if (position != null && position >= 0) {
                val ingredient = it[position]
                nameEditText.editText?.setText(ingredient.name, TextView.BufferType.EDITABLE)
                amountEditText.editText?.setText(
                    ingredient.amount.toString(),
                    TextView.BufferType.EDITABLE
                )
                priceEditText.editText?.setText(
                    String.format("%.2f",ingredient.price),
                    TextView.BufferType.EDITABLE
                )
            }
        }

        btnAddToPantry.setOnClickListener {
            Log.d("EDIT INGREDIENT", "onCreateView: $position")
            saveIngredient()
        }

        btnCancel.setOnClickListener {
            view.findNavController().navigate(R.id.navigation_recipes)
        }
        return view
    }

    private fun saveIngredient() {
        Log.d("SAVING", "saveIngredient: ")
    }

}