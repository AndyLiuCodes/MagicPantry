package com.ala158.magicpantry.ui.manualingredientinput

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.ala158.magicpantry.IngredientEntry
import com.ala158.magicpantry.R
import com.ala158.magicpantry.dao.IngredientDAO
import com.ala158.magicpantry.database.MagicPantryDatabase
import com.ala158.magicpantry.repository.IngredientRepository
import com.ala158.magicpantry.viewModel.IngredientViewModelFactory
import com.google.android.material.textfield.TextInputEditText


class ManualIngredientInputFragment : Fragment() {
    private lateinit var btnAddToPantry: Button
    private lateinit var btnCancel: Button
    private lateinit var ingredientNameTextField: TextInputEditText
    private lateinit var amountTextField: TextInputEditText
    private lateinit var unitEditDropdown: AutoCompleteTextView
    private lateinit var priceTextField: TextInputEditText
    private lateinit var database: MagicPantryDatabase
    private lateinit var ingredientDAO: IngredientDAO
    private lateinit var repository: IngredientRepository
    private lateinit var ingredientViewModelFactory: IngredientViewModelFactory
    private lateinit var manualIngredientsInputViewModel: ManualIngredientInputViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_manual_ingredient_input, container, false)
        btnAddToPantry = view.findViewById(R.id.btn_add_to_pantry)
        btnCancel = view.findViewById(R.id.btn_cancel_pantry)
        ingredientNameTextField = view.findViewById(R.id.manual_input_ingredient_name)
        amountTextField = view.findViewById(R.id.manual_input_amount)
        unitEditDropdown = view.findViewById(R.id.manual_input_unit)
        priceTextField = view.findViewById(R.id.manual_input_price)

        database = MagicPantryDatabase.getInstance(requireActivity())
        ingredientDAO = database.ingredientDAO
        repository = IngredientRepository(ingredientDAO)
        ingredientViewModelFactory = IngredientViewModelFactory(repository)
        manualIngredientsInputViewModel =
            ViewModelProvider(
                requireActivity(),
                ingredientViewModelFactory
            ).get(ManualIngredientInputViewModel::class.java)

        initTextWatchers()

        manualIngredientsInputViewModel.ingredient.observe(requireActivity()) {
            ingredientNameTextField.setText(it.getName())
            amountTextField.setText(it.getAmount().toString())
            unitEditDropdown.listSelection = UNIT_DROPDOWN_MAPPING[it.getUnit()]!!
            priceTextField.setText(it.getPrice().toString())
        }
        btnAddToPantry.setOnClickListener {
            addItemToPantry()
        }

        btnCancel.setOnClickListener {
            view.findNavController().navigate(R.id.navigation_pantry)
        }
        return view
    }

    private fun initTextWatchers() {
        ingredientNameTextField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                manualIngredientsInputViewModel.ingredient.value!!.setName(s.toString())
                return
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }
        })

        amountTextField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val amountString = s.toString()
                var amount = 0
                if (amountString != "")
                    amount = amountString.toInt()
                manualIngredientsInputViewModel.ingredient.value!!.setAmount(amount)
                return
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }
        })

        unitEditDropdown.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                manualIngredientsInputViewModel.ingredient.value!!.setUnit(s.toString())
                return
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }
        })

        priceTextField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val priceString = s.toString()
                var price = 0.0
                if (priceString != "")
                    price = priceString.toDouble()
                manualIngredientsInputViewModel.ingredient.value!!.setPrice(price)
                return
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }
        })
    }

    private fun addItemToPantry() {
        // Add error checking
        manualIngredientsInputViewModel.insertIngredient()
        // TODO swap fragment to a activity because pressing Add Item, does not remove the entry
        manualIngredientsInputViewModel.ingredient.value = IngredientEntry()
        requireActivity().supportFragmentManager.popBackStack()
    }

    companion object {
        val UNIT_DROPDOWN_MAPPING = mapOf<String, Int>(
            "kg"    to 0,
            "g"     to 1,
            "ml"    to 3,
            "L"     to 4,
            "unit"  to 5
        )
    }
}