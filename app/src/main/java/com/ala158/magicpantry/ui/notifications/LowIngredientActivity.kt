package com.ala158.magicpantry.ui.notifications

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.arrayAdapter.LowIngredientArrayAdapter

class LowIngredientActivity : AppCompatActivity() {
    private lateinit var ingredientList : ListView
    private var ingredients = arrayOf("bread", "milk")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_low_ingredient)

        ingredientList = findViewById(R.id.notifications_list_view)
        val adapter = LowIngredientArrayAdapter(this, ingredients)
        ingredientList.adapter = adapter

        val addAllBtn = findViewById<Button>(R.id.btn_add_ingredient_notifications)
        addAllBtn.setOnClickListener {
            saveToDatabase()
        }
    }

    private fun saveToDatabase() {
        //TODO:
    }
}