package com.ala158.magicpantry.ui.manualingredientinput.edit

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.ui.reviewingredients.ReviewIngredientsViewModel
import com.google.android.material.textfield.TextInputLayout

class ReviewIngredientsEditActivity : AppCompatActivity() {
    private lateinit var nameEditText: TextInputLayout
    private lateinit var amountEditText: TextInputLayout
    private lateinit var unitEditDropdown: AutoCompleteTextView
    private lateinit var priceEditText: TextInputLayout
    private lateinit var btnAddToPantry: Button
    private lateinit var btnCancel: Button
    private var position = -1

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var reviewIngredientsViewModel: ReviewIngredientsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_manual_ingredient_input_edit)

        btnAddToPantry = findViewById(R.id.btn_add_to_pantry)
        btnCancel = findViewById(R.id.btn_cancel_pantry)
        nameEditText = findViewById(R.id.editName)
        amountEditText = findViewById(R.id.editAmount)
        unitEditDropdown = findViewById(R.id.editUnitDropdown)
        priceEditText = findViewById(R.id.editPrice)

        position = intent.extras?.getBundle("pos")?.getInt("position")!!
        val name = intent.getStringExtra("name")
        val amount = intent.getIntExtra("amount", 0)
        val price = intent.getDoubleExtra("price", 0.0)
        val unit = intent.getStringExtra("unit")

        reviewIngredientsViewModel = Util.createViewModel(
            this,
            ReviewIngredientsViewModel::class.java,
            Util.DataType.INGREDIENT
        )

        if (position >= 0) {
            nameEditText.editText?.setText(name, TextView.BufferType.EDITABLE)
            amountEditText.editText?.setText(
                amount.toString(),
                TextView.BufferType.EDITABLE
            )
            unitEditDropdown.setText(unit, TextView.BufferType.EDITABLE)
            priceEditText.editText?.setText(
                String.format("%.2f", price),
                TextView.BufferType.EDITABLE
            )
        }

        btnAddToPantry.setOnClickListener {
            saveIngredient()
            onBackPressed()
        }

        btnCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun saveIngredient() {
        val name = nameEditText.editText?.text.toString()
        val amount = amountEditText.editText?.text.toString().toInt()
        val unit = unitEditDropdown.text.toString()
        val price = priceEditText.editText?.text.toString().toDouble()

        sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()

        edit.putInt("currPos", position)
        edit.putString("name", name)
        edit.putInt("amount", amount)
        edit.putString("price", price.toString())
        edit.putString("unit", unit)
        edit.apply()
//        val updatedIngredient = Ingredient(name, amount, unit, price)
//        reviewIngredientsViewModel.updateIngredient(position, updatedIngredient)
    }
}