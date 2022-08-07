package com.ala158.magicpantry.ui.manualingredientinput.edit

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.ui.manualingredientinput.ManualIngredientInputActivity
import com.ala158.magicpantry.ui.manualingredientinput.ManualIngredientInputViewModel
import com.ala158.magicpantry.ui.reviewingredients.ReviewIngredientsViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ReviewIngredientsEditActivity : AppCompatActivity() {
    private lateinit var btnSave: Button
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
    private var isIngredientNameValid = true
    private var isAmountValid = true
    private var isPricePerUnitValid = true

    private var position = -1
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var reviewIngredientsViewModel: ReviewIngredientsViewModel
    private var isFirstStart = true // prevent intent data overwriting user edits
    private var unit: String = "unit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_ingredients_edit)

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE)
        reviewIngredientsViewModel = Util.createViewModel(
            this,
            ReviewIngredientsViewModel::class.java,
            Util.DataType.INGREDIENT
        )

        if (isFirstStart) {
            position = intent.extras?.getBundle(BUNDLE_POS_KEY)?.getInt(BUNDLE_POSITION_KEY)!!
            var name = intent.getStringExtra(NAME_KEY)
            val amount = intent.getDoubleExtra(AMOUNT_KEY, 0.0)
            val price = intent.getDoubleExtra(PRICE_KEY, 0.0)
            unit = intent.getStringExtra(UNIT_KEY).toString()

            if (position >= 0) {
                if (name == null) {
                    name = ""
                }
                reviewIngredientsViewModel.ingredient.value!!.setName(name)
                reviewIngredientsViewModel.ingredient.value!!.setAmount(amount)

                if (unit == null) {
                    unit = "unit"
                }
                reviewIngredientsViewModel.ingredient.value!!.setUnit(unit)

                reviewIngredientsViewModel.ingredient.value!!.setPrice(price)
            }

            isFirstStart = false
        }

        btnSave = findViewById(R.id.btn_add_to_pantry)
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

        unitEditDropdown.setSelection(UNIT_DROPDOWN_MAPPING[unit]!!)
        lowStockThresholdUnitTextView.text = unitEditDropdown.selectedItem.toString()

        priceTextField = findViewById(R.id.manual_input_price)
        priceLabel = findViewById(R.id.manual_price_label)

        // Updating the text inside the EditText fields
        ingredientNameTextField.setText(reviewIngredientsViewModel.ingredient.value!!.getName())
        amountTextField.setText(
            reviewIngredientsViewModel.ingredient.value!!.getAmount().toString()
        )
        unitEditDropdown.setSelection(
            UNIT_DROPDOWN_MAPPING[reviewIngredientsViewModel.ingredient.value!!.getUnit()]!!
        )
        lowStockThresholdUnitTextView.text = reviewIngredientsViewModel.ingredient.value!!.getUnit()
        priceTextField.setText(reviewIngredientsViewModel.ingredient.value!!.getPrice().toString())
        lowStockThresholdField.setText(
            reviewIngredientsViewModel.ingredient.value!!.getNotifyThreshold().toString()
        )
        if (reviewIngredientsViewModel.ingredient.value!!.getIsNotify()) {
            isNotifyCheckBoxView.isChecked = true
            thresholdSectionLayout.visibility = View.VISIBLE
        } else {
            isNotifyCheckBoxView.isChecked = false
            thresholdSectionLayout.visibility = View.INVISIBLE
        }

        initTextWatchers()

        isNotifyCheckBoxView.setOnCheckedChangeListener() { _, isChecked ->

            if (isChecked) {
                thresholdSectionLayout.visibility = View.VISIBLE
            } else {
                // Reset the threshold data if the user unchecks the notify when low on stock
                lowStockThresholdField.setText("")
                reviewIngredientsViewModel.ingredient.value!!.setNotifyThreshold(0.0)
                thresholdSectionLayout.visibility = View.INVISIBLE
            }

            reviewIngredientsViewModel.ingredient.value!!.setIsNotify(isChecked)
        }

        btnSave.setOnClickListener {
            saveIngredient()
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

    private fun saveIngredient() {
        if (validateIngredientInput()) {
            val name = reviewIngredientsViewModel.ingredient.value!!.getName()
            val amount = reviewIngredientsViewModel.ingredient.value!!.getAmount()
            val unit = reviewIngredientsViewModel.ingredient.value!!.getUnit()
            val price = reviewIngredientsViewModel.ingredient.value!!.getPrice()
            val isNotify = reviewIngredientsViewModel.ingredient.value!!.getIsNotify()
            val notifyThreshold = reviewIngredientsViewModel.ingredient.value!!.getNotifyThreshold()

            val edit = sharedPreferences.edit()

            edit.putInt(CURRENT_POSITION_KEY, position)
            edit.putBoolean(DELETE_INGREDIENT_KEY, false)
            edit.putString(NAME_KEY, name)
            edit.putString(AMOUNT_KEY, amount.toString())
            edit.putString(PRICE_KEY, price.toString())
            edit.putString(UNIT_KEY, unit)
            edit.putBoolean(IS_NOTIFY_KEY, isNotify)
            edit.putString(NOTIFY_THRESHOLD_KEY, notifyThreshold.toString())
            edit.apply()
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun validateIngredientInput(): Boolean {
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.manual_ingredient_input_action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_menu_manualinput_delete) {
            Log.d("DELETE PLEASE", "onOptionsItemSelected: DELETE FROM INGREDIENT EDIT")
            val edit = sharedPreferences.edit()
            edit.putInt(CURRENT_POSITION_KEY, position)
            edit.putBoolean(DELETE_INGREDIENT_KEY, true)
            edit.apply()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initTextWatchers() {
        ingredientNameTextField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                ingredientNameLabel.setTextColor(resources.getColor(R.color.black, null))
                isIngredientNameValid = true
                reviewIngredientsViewModel.ingredient.value!!.setName(s.toString())
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
                reviewIngredientsViewModel.ingredient.value!!.setAmount(amount)
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
                reviewIngredientsViewModel.ingredient.value!!.setUnit(unitString)
                lowStockThresholdUnitTextView.text = unitString
            }
        }

        priceTextField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val priceString = s.toString()
                var price = 0.0
                if (priceString != "" && priceString != ".")
                    price = priceString.toDouble()

                reviewIngredientsViewModel.ingredient.value!!.setPrice(price)

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

        lowStockThresholdField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val thresholdAmountString = s.toString()
                var thresholdAmount = 0.0
                if (thresholdAmountString != "")
                    thresholdAmount = thresholdAmountString.toDouble()

                reviewIngredientsViewModel.ingredient.value!!.setNotifyThreshold(thresholdAmount)
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

    companion object {
        val UNIT_DROPDOWN_MAPPING = mapOf<String, Int>(
            "kg" to 0,
            "g" to 1,
            "ml" to 2,
            "L" to 3,
            "unit" to 4
        )

        const val DELETE_INGREDIENT_KEY = "DELETE_INGREDIENT_KEY"
        val IS_INGREDIENT_NAME_VALID_KEY = "IS_INGREDIENT_NAME_VALID_KEY"
        val IS_AMOUNT_VALID_KEY = "IS_AMOUNT_VALID_KEY"
        val IS_PRICE_PER_UNIT_VALID_KEY = "IS_PRICE_PER_UNIT_VALID_KEY"
        val CURRENT_POSITION_KEY = "CURRENT_POSITION_KEY"
        val NAME_KEY = "NAME_KEY"
        val AMOUNT_KEY = "AMOUNT_KEY"
        val PRICE_KEY = "PRICE_KEY"
        val UNIT_KEY = "UNIT_KEY"
        val IS_NOTIFY_KEY = "IS_NOTIFY_KEY"
        val NOTIFY_THRESHOLD_KEY = "NOTIFY_THRESHOLD_KEY"
        val SHARED_PREFERENCE_KEY = "MySharedPrefs"
        val BUNDLE_POS_KEY = "BUNDLE_POS_KEY"
        val BUNDLE_POSITION_KEY = "BUNDLE_POSITION_KEY"
    }
}