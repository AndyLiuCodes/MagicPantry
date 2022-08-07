package com.ala158.magicpantry.ui.manualingredientinput

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.google.android.material.textfield.TextInputEditText

class ManualIngredientInputActivity : AppCompatActivity() {
    private lateinit var btnAddToPantry: Button
    private lateinit var btnCancel: Button
    private lateinit var ingredientNameLabel: TextView
    private lateinit var ingredientNameTextField: EditText
    private lateinit var amountLabel: TextView
    private lateinit var amountTextField: EditText
    private lateinit var unitEditDropdown: Spinner
    private lateinit var priceLabel: TextView
    private lateinit var priceTextField: EditText
    private lateinit var lowStockThresholdField: EditText
    private lateinit var lowStockThresholdUnitTextView: TextView
    private lateinit var isNotifyCheckBoxView: CheckBox
    private lateinit var thresholdSectionLayout: LinearLayout
    private lateinit var manualIngredientsInputViewModel: ManualIngredientInputViewModel
    private var isIngredientNameValid = true
    private var isAmountValid = true
    private var isPricePerUnitValid = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_ingredient_input)
        btnAddToPantry = findViewById(R.id.btn_add_to_pantry)
        btnCancel = findViewById(R.id.btn_cancel_pantry)
        ingredientNameLabel = findViewById(R.id.manual_ingredient_name_label)
        ingredientNameTextField = findViewById(R.id.manual_input_ingredient_name)
        amountTextField = findViewById(R.id.manual_input_amount)
        amountLabel = findViewById(R.id.manual_input_amount_label)
        lowStockThresholdField = findViewById(R.id.manual_input_threshold)
        lowStockThresholdUnitTextView = findViewById(R.id.manual_input_threshold_unit)
        isNotifyCheckBoxView = findViewById(R.id.manual_checkbox_isnotify)
        thresholdSectionLayout = findViewById(R.id.manual_threshold_section)

        val unitAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.unit_items,
            R.layout.spinner_item_unit_dropdown
        )
        unitAdapter.setDropDownViewResource(R.layout.spinner_item_unit_dropdown)
        unitEditDropdown = findViewById(R.id.manual_input_unit)
        unitEditDropdown.adapter = unitAdapter
        // Default unit is unit
        unitEditDropdown.setSelection(UNIT_DROPDOWN_MAPPING["unit"]!!)
        lowStockThresholdUnitTextView.text = unitEditDropdown.selectedItem.toString()

        priceTextField = findViewById(R.id.manual_input_price)
        priceLabel = findViewById(R.id.manual_price_label)

        manualIngredientsInputViewModel = Util.createViewModel(
            this,
            ManualIngredientInputViewModel::class.java,
            Util.DataType.INGREDIENT
        )

        initTextWatchers()

        isNotifyCheckBoxView.setOnCheckedChangeListener() {
            _, isChecked ->

            if (isChecked) {
                thresholdSectionLayout.visibility = View.VISIBLE
            } else {
                // Reset the threshold data if the user unchecks the notify when low on stock
                lowStockThresholdField.setText("")
                manualIngredientsInputViewModel.ingredient.value!!.setNotifyThreshold(0.0)
                thresholdSectionLayout.visibility = View.INVISIBLE
            }

            manualIngredientsInputViewModel.ingredient.value!!.setIsNotify(isChecked)
        }

        btnAddToPantry.setOnClickListener {
            addItemToPantry()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_INGREDIENT_NAME_VALID_KEY, isIngredientNameValid)
        outState.putBoolean(IS_AMOUNT_VALID_KEY, isAmountValid)
        outState.putBoolean(IS_PRICE_PER_UNIT_VALID_KEY, isPricePerUnitValid)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            isIngredientNameValid = savedInstanceState.getBoolean(IS_INGREDIENT_NAME_VALID_KEY)
            if (!isIngredientNameValid)
                ingredientNameLabel.setTextColor(resources.getColor(R.color.mp_red, null))
            isAmountValid = savedInstanceState.getBoolean(IS_AMOUNT_VALID_KEY)
            if (!isAmountValid)
                amountLabel.setTextColor(resources.getColor(R.color.mp_red, null))
            isPricePerUnitValid = savedInstanceState.getBoolean(IS_PRICE_PER_UNIT_VALID_KEY)
            if (!isPricePerUnitValid)
                priceLabel.setTextColor(resources.getColor(R.color.mp_red, null))
        }
    }

    private fun initTextWatchers() {
        ingredientNameTextField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                ingredientNameLabel.setTextColor(resources.getColor(R.color.black, null))
                isIngredientNameValid = true
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
                var amount = 0.0
                if (amountString != "")
                    amount = amountString.toDouble()
                manualIngredientsInputViewModel.ingredient.value!!.setAmount(amount)
                amountLabel.setTextColor(resources.getColor(R.color.black, null))
                isAmountValid = true
                return
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }
        })

        // Help from https://stackoverflow.com/a/49376648 // for setting up the on item selected listener
        unitEditDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                return
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val unitString = parent!!.getItemAtPosition(position).toString()
                manualIngredientsInputViewModel.ingredient.value!!.setUnit(unitString)
                lowStockThresholdUnitTextView.text = unitString
            }
        }

        priceTextField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val priceString = s.toString()
                var price = 0.0
                if (priceString != "" && priceString != ".")
                    price = priceString.toDouble()

                manualIngredientsInputViewModel.ingredient.value!!.setPrice(price)

                priceLabel.setTextColor(resources.getColor(R.color.black, null))
                isPricePerUnitValid = true
                return
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }
        })

        lowStockThresholdField.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val thresholdAmountString = s.toString()
                var thresholdAmount = 0.0
                if (thresholdAmountString != "")
                    thresholdAmount = thresholdAmountString.toDouble()

                manualIngredientsInputViewModel.ingredient.value!!.setNotifyThreshold(thresholdAmount)
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
        if (validateIngredientInput()) {
            manualIngredientsInputViewModel.insertIngredient()
            Toast.makeText(this, "Ingredient added!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun validateIngredientInput() : Boolean {
        var errorMsg = ""

        if (ingredientNameTextField.text.toString().trim() == "") {
            errorMsg += "• The ingredient name cannot be empty"
            ingredientNameLabel.setTextColor(resources.getColor(R.color.mp_red, null))
            isIngredientNameValid = false
        }

        if (amountTextField.text.toString() == "") {
            if (errorMsg != "")
                errorMsg += "\n"
            errorMsg += "• The amount of ingredient cannot be empty"
            amountLabel.setTextColor(resources.getColor(R.color.mp_red, null))
            isAmountValid = false
        }

        if (priceTextField.text.toString() == ".") {
            if (errorMsg != "")
                errorMsg += "\n"
            errorMsg += "• The price per unit is invalid"
            priceLabel.setTextColor(resources.getColor(R.color.mp_red, null))
            isPricePerUnitValid = false
        }

        if (errorMsg != "") {
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    companion object {
        val UNIT_DROPDOWN_MAPPING = mapOf<String, Int>(
            "kg"    to 0,
            "g"     to 1,
            "ml"    to 2,
            "L"     to 3,
            "unit"  to 4
        )

        val IS_INGREDIENT_NAME_VALID_KEY = "IS_INGREDIENT_NAME_VALID_KEY"
        val IS_AMOUNT_VALID_KEY = "IS_AMOUNT_VALID_KEY"
        val IS_PRICE_PER_UNIT_VALID_KEY = "IS_PRICE_PER_UNIT_VALID_KEY"
    }
}