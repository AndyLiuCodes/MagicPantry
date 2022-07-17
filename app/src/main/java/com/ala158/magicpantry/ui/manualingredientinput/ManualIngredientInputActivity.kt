package com.ala158.magicpantry.ui.manualingredientinput

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.ala158.magicpantry.R
import com.ala158.magicpantry.dao.IngredientDAO
import com.ala158.magicpantry.database.MagicPantryDatabase
import com.ala158.magicpantry.repository.MagicPantryRepository
import com.ala158.magicpantry.viewModel.ViewModelFactory
import com.google.android.material.textfield.TextInputEditText

class ManualIngredientInputActivity : AppCompatActivity() {
    private lateinit var btnAddToPantry: Button
    private lateinit var btnCancel: Button
    private lateinit var ingredientNameTextField: TextInputEditText
    private lateinit var amountTextField: TextInputEditText
    private lateinit var unitEditDropdown: AutoCompleteTextView
    private lateinit var priceTextField: TextInputEditText
    private lateinit var database: MagicPantryDatabase
    private lateinit var ingredientDAO: IngredientDAO
    private lateinit var repository: MagicPantryRepository
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var manualIngredientsInputViewModel: ManualIngredientInputViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_ingredient_input)
        btnAddToPantry = findViewById(R.id.btn_add_to_pantry)
        btnCancel = findViewById(R.id.btn_cancel_pantry)
        ingredientNameTextField = findViewById(R.id.manual_input_ingredient_name)
        amountTextField = findViewById(R.id.manual_input_amount)
        unitEditDropdown = findViewById(R.id.manual_input_unit)
        priceTextField = findViewById(R.id.manual_input_price)

        database = MagicPantryDatabase.getInstance(this)
        ingredientDAO = database.ingredientDAO
        repository = MagicPantryRepository(ingredientDAO)
        viewModelFactory = ViewModelFactory(repository)
        manualIngredientsInputViewModel =
            ViewModelProvider(
                this,
                viewModelFactory
            ).get(ManualIngredientInputViewModel::class.java)

        initTextWatchers()

        btnAddToPantry.setOnClickListener {
            addItemToPantry()
        }

        btnCancel.setOnClickListener {
            finish()
        }
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
        finish()
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