package com.ala158.magicpantry.ui.manualingredientinput.edit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.ala158.magicpantry.R
import com.ala158.magicpantry.dao.IngredientDAO
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.database.MagicPantryDatabase
import com.ala158.magicpantry.repository.IngredientRepository
import com.ala158.magicpantry.ui.reviewingredients.ReviewIngredientsViewModel
import com.ala158.magicpantry.viewModel.IngredientViewModelFactory
import com.google.android.material.textfield.TextInputLayout

class ManualIngredientInputEditFragment : Fragment() {
    private lateinit var nameEditText: TextInputLayout
    private lateinit var amountEditText: TextInputLayout
    private lateinit var unitEditDropdown: AutoCompleteTextView
    private lateinit var priceEditText: TextInputLayout
    private lateinit var btnAddToPantry: Button
    private lateinit var btnCancel: Button
    private var position = -1

    private lateinit var database: MagicPantryDatabase
    private lateinit var ingredientDAO: IngredientDAO
    private lateinit var repository: IngredientRepository
    private lateinit var ingredientViewModelFactory: IngredientViewModelFactory
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
        // FIXME Dropdown does not show all items of array
        unitEditDropdown = view.findViewById(R.id.editUnitDropdown)
        priceEditText = view.findViewById(R.id.editPrice)

        position = arguments?.getInt("position")!!

        database = MagicPantryDatabase.getInstance(requireActivity())
        ingredientDAO = database.ingredientDAO
        repository = IngredientRepository(ingredientDAO)
        ingredientViewModelFactory = IngredientViewModelFactory(repository)
        reviewIngredientsViewModel =
            ViewModelProvider(
                requireActivity(),
                ingredientViewModelFactory
            ).get(ReviewIngredientsViewModel::class.java)

        reviewIngredientsViewModel.ingredientList.observe(requireActivity()) {
            if (position >= 0) {
                val ingredient = it[position]
                nameEditText.editText?.setText(ingredient.name, TextView.BufferType.EDITABLE)
                amountEditText.editText?.setText(
                    ingredient.amount.toString(),
                    TextView.BufferType.EDITABLE
                )
                unitEditDropdown.setText(ingredient.unit,TextView.BufferType.EDITABLE)
                priceEditText.editText?.setText(
                    String.format("%.2f",ingredient.price),
                    TextView.BufferType.EDITABLE
                )
            }
        }

        btnAddToPantry.setOnClickListener {
            saveIngredient()
            view.findNavController().navigate(R.id.navigation_recipes)
        }

        btnCancel.setOnClickListener {
            view.findNavController().navigate(R.id.navigation_recipes)
        }
        return view
    }

    private fun saveIngredient() {
        val name = nameEditText.editText?.text.toString()
        val amount = amountEditText.editText?.text.toString().toInt()
        val unit = unitEditDropdown.text.toString()
        val price = priceEditText.editText?.text.toString().toDouble()

        val updatedIngredient = Ingredient(name, amount, unit, price)
        reviewIngredientsViewModel.updateIngredient(position, updatedIngredient)
    }

}