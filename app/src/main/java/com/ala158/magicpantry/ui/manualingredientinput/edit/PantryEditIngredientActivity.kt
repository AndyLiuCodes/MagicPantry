package com.ala158.magicpantry.ui.manualingredientinput.edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.ui.pantry.PantryFragment
import com.google.android.material.textfield.TextInputEditText

class PantryEditIngredientActivity : AppCompatActivity() {
    private lateinit var ingredientNameLabel: TextView
    private lateinit var textInputEditIngredientName: TextInputEditText
    private lateinit var amountLabel: TextView
    private lateinit var textInputEditAmount: TextInputEditText
    private lateinit var unitDropdown: Spinner
    private lateinit var priceLabel: TextView
    private lateinit var textInputEditPrice: TextInputEditText
    private lateinit var lowStockThresholdField: TextInputEditText
    private lateinit var lowStockThresholdUnitTextView: TextView
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private lateinit var pantryEditIngredientViewModel: PantryEditIngredientViewModel

    private var ingredientId = -1L
    private var isIngredientNameValid = true
    private var isAmountValid = true
    private var isPricePerUnitValid = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantry_edit_ingredient)
        ingredientId = intent.getLongExtra(PantryFragment.PANTRY_INGREDIENT_ID_KEY, -1L)

        initViews()
        initDatabaseAndViewModel()
        initTextWatchers()

        pantryEditIngredientViewModel.ingredientEntry.observe(this) {
            // Depending on what the ingredient's unit are, we limit what they can change the unit to
            val unitAdapter = getAppropriateUnitAdapter(it.getUnit())
            unitAdapter.setDropDownViewResource(R.layout.spinner_item_unit_dropdown)
            unitDropdown.adapter = unitAdapter

            textInputEditIngredientName.setText(it.getName())
            textInputEditAmount.setText(it.getAmount().toString())
            // Had help from https://stackoverflow.com/a/57119977 for setting the dropdown value
            val dropdownMapping = getAppropriateDropdownMapping(it.getUnit())
            unitDropdown.setSelection(dropdownMapping[it.getUnit()]!!)
            lowStockThresholdUnitTextView.text = it.getUnit()

            if (it.getUnit() == "unit") {
                // Disable the unit dropdown when the ingredient uses unit type "unit"
                unitDropdown.isEnabled = false
                unitDropdown.isClickable = false
            }

            if (it.getPrice() != 0.0)
                textInputEditPrice.setText(it.getPrice().toString())

            if (it.getNotifyThreshold() != 0) {
                lowStockThresholdField.setText(it.getNotifyThreshold().toString())
            }
        }

        if (pantryEditIngredientViewModel.ingredientEntry.value == null) {
            pantryEditIngredientViewModel.getIngredientEntry(ingredientId)
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            if (validateIngredientInput()) {
                pantryEditIngredientViewModel.updateIngredientEntry(ingredientId)
                Toast.makeText(this, "Saved ingredient!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun getAppropriateUnitAdapter(unit: String) : ArrayAdapter<CharSequence> {
        if (unit == "kg" || unit == "g") {
            return ArrayAdapter.createFromResource(
                this,
                R.array.unit_mass,
                R.layout.spinner_item_unit_dropdown
            )
        }

        if (unit == "L" || unit == "mL") {
            return ArrayAdapter.createFromResource(
                this,
                R.array.unit_volume,
                R.layout.spinner_item_unit_dropdown
            )
        }

        return ArrayAdapter.createFromResource(
            this,
            R.array.unit_unit,
            R.layout.spinner_item_unit_dropdown
        )
    }

    private fun getAppropriateDropdownMapping(unit: String) : Map<String, Int> {
        if (unit == "kg" || unit == "g") {
            return UNIT_MASS_DROPDOWN_MAPPING
        }

        if (unit == "L" || unit == "mL") {
            return UNIT_VOLUME_DROPDOWN_MAPPING
        }

        return UNIT_UNIT_DROPDOWN_MAPPING
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

    private fun initViews() {
        textInputEditIngredientName = findViewById(R.id.pantry_edit_name)
        textInputEditAmount = findViewById(R.id.pantry_edit_amount)
        unitDropdown = findViewById(R.id.pantry_edit_unit_dropdown)
        textInputEditPrice = findViewById(R.id.pantry_edit_price)
        ingredientNameLabel = findViewById(R.id.ingredient_edit_name_label)
        amountLabel = findViewById(R.id.ingredient_edit_amount_label)
        priceLabel = findViewById(R.id.ingredient_edit_price_label)
        lowStockThresholdField = findViewById(R.id.pantry_edit_threshold)
        lowStockThresholdUnitTextView = findViewById(R.id.pantry_edit_threshold_unit)
        btnCancel = findViewById(R.id.btn_cancel_pantry_edit)
        btnSave = findViewById(R.id.btn_save_pantry_edit)
    }

    private fun initDatabaseAndViewModel() {
        pantryEditIngredientViewModel = Util.createViewModel(
            this,
            PantryEditIngredientViewModel::class.java,
            Util.DataType.INGREDIENT
        )
    }

    private fun initTextWatchers() {
        textInputEditIngredientName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                pantryEditIngredientViewModel.ingredientEntry.value!!.setName(s.toString())
                ingredientNameLabel.setTextColor(resources.getColor(R.color.mp_textview_grey, null))
                isIngredientNameValid = true
                return
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }
        })

        textInputEditAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val amountString = s.toString()
                var amount = 0.0
                if (amountString != "")
                    amount = amountString.toDouble()
                pantryEditIngredientViewModel.ingredientEntry.value!!.setAmount(amount)
                amountLabel.setTextColor(resources.getColor(R.color.mp_textview_grey, null))
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
        unitDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                pantryEditIngredientViewModel.ingredientEntry.value!!.setUnit(unitString)
                lowStockThresholdUnitTextView.text = unitString
            }
        }

        textInputEditPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val priceString = s.toString()
                var price = 0.0
                if (priceString != "" && priceString != ".")
                    price = priceString.toDouble()
                pantryEditIngredientViewModel.ingredientEntry.value!!.setPrice(price)
                priceLabel.setTextColor(resources.getColor(R.color.mp_textview_grey, null))
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
                var thresholdAmount = 0
                if (thresholdAmountString != "")
                    thresholdAmount = thresholdAmountString.toInt()

                pantryEditIngredientViewModel.ingredientEntry.value!!.setNotifyThreshold(thresholdAmount)
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

    // Used the following resource to create the options menu:
    // https://www.techotopia.com/index.php/Creating_and_Managing_Overflow_Menus_on_Android_with_Kotlin
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.manual_ingredient_input_action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Had help from https://developer.android.com/training/appbar/actions#handle-actions
    // detecting when a user presses on an item on the top menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_menu_manualinput_delete && ingredientId != -1L) {
            pantryEditIngredientViewModel.deleteIngredientEntry(ingredientId)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validateIngredientInput() : Boolean {
        var errorMsg = ""

        if (textInputEditIngredientName.text.toString().trim() == "") {
            errorMsg += "• The ingredient name cannot be empty"
            ingredientNameLabel.setTextColor(resources.getColor(R.color.mp_red, null))
            isIngredientNameValid = false
        }

        if (textInputEditAmount.text.toString() == "") {
            if (errorMsg != "")
                errorMsg += "\n"
            errorMsg += "• The amount of ingredient cannot be empty"
            amountLabel.setTextColor(resources.getColor(R.color.mp_red, null))
            isAmountValid = false
        }

        if (textInputEditPrice.text.toString() == ".") {
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
        val IS_INGREDIENT_NAME_VALID_KEY = "IS_INGREDIENT_NAME_VALID_KEY"
        val IS_AMOUNT_VALID_KEY = "IS_AMOUNT_VALID_KEY"
        val IS_PRICE_PER_UNIT_VALID_KEY = "IS_PRICE_PER_UNIT_VALID_KEY"

        val UNIT_DROPDOWN_MAPPING = mapOf<String, Int>(
            "kg"    to 0,
            "g"     to 1,
            "mL"    to 2,
            "L"     to 3,
            "unit"  to 4
        )

        val UNIT_VOLUME_DROPDOWN_MAPPING = mapOf<String, Int>(
            "mL"    to 0,
            "L"     to 1
        )

        val UNIT_MASS_DROPDOWN_MAPPING = mapOf<String, Int>(
            "kg"    to 0,
            "g"     to 1
        )

        val UNIT_UNIT_DROPDOWN_MAPPING = mapOf<String, Int>(
            "unit"    to 0
        )
    }
}