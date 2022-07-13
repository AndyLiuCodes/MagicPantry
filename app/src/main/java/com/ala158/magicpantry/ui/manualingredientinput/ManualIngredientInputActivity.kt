package com.ala158.magicpantry.ui.manualingredientinput

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.ala158.magicpantry.R

class ManualIngredientInputActivity : AppCompatActivity() {
    private lateinit var btnAddToPantry: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_ingredient_input)

        btnAddToPantry = findViewById(R.id.btn_add_to_pantry)

        btnAddToPantry.setOnClickListener {
            addItemToPantry()
        }

    }

    private fun addItemToPantry() {
        // TODO
        finish()
    }
}