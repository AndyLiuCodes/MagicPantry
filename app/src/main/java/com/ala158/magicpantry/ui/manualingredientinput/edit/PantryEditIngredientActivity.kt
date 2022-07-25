package com.ala158.magicpantry.ui.manualingredientinput.edit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.ui.pantry.PantryFragment
import com.google.android.material.textfield.TextInputEditText

class PantryEditIngredientActivity : AppCompatActivity() {
    private lateinit var textInputEditIngredientName: TextInputEditText
    private lateinit var textInputEditAmount: TextInputEditText
    private lateinit var autoCompleteUnitDropdown: AutoCompleteTextView
    private lateinit var textInputEditPrice: TextInputEditText
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private lateinit var pantryEditIngredientViewModel: PantryEditIngredientViewModel

    private var ingredientId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantry_edit_ingredient)
        ingredientId = intent.getLongExtra(PantryFragment.PANTRY_INGREDIENT_ID_KEY, -1L)

        initViews()
        initDatabaseAndViewModel()
        initTextWatchers()

        pantryEditIngredientViewModel.ingredientEntry.observe(this) {
            textInputEditIngredientName.setText(it.getName())
            textInputEditAmount.setText(it.getAmount().toString())
            // Had help from https://stackoverflow.com/a/57119977 for setting the dropdown value
            autoCompleteUnitDropdown.setText(it.getUnit(), false)
            textInputEditPrice.setText(it.getPrice().toString())
        }

        if (pantryEditIngredientViewModel.ingredientEntry.value == null) {
            pantryEditIngredientViewModel.getIngredientEntry(ingredientId)
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            pantryEditIngredientViewModel.updateIngredientEntry(ingredientId)
            Toast.makeText(this, "Saved ingredient!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initViews() {
        textInputEditIngredientName = findViewById(R.id.pantry_edit_name)
        textInputEditAmount = findViewById(R.id.pantry_edit_amount)
        autoCompleteUnitDropdown = findViewById(R.id.pantry_edit_unit_dropdown)
        textInputEditPrice = findViewById(R.id.pantry_edit_price)
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
                var amount = 0
                if (amountString != "")
                    amount = amountString.toInt()
                pantryEditIngredientViewModel.ingredientEntry.value!!.setAmount(amount)
                return
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }
        })

        autoCompleteUnitDropdown.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                pantryEditIngredientViewModel.ingredientEntry.value!!.setUnit(s.toString())
                return
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }
        })

        textInputEditPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val priceString = s.toString()
                var price = 0.0
                if (priceString != "")
                    price = priceString.toDouble()
                pantryEditIngredientViewModel.ingredientEntry.value!!.setPrice(price)
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
}