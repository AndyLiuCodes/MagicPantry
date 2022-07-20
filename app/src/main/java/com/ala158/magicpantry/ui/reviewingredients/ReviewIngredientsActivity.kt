package com.ala158.magicpantry.ui.reviewingredients

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ala158.magicpantry.MainActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.arrayAdapter.ReviewIngredientsActivityAdapter
import com.ala158.magicpantry.dao.IngredientDAO
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.database.MagicPantryDatabase
import com.ala158.magicpantry.repository.MagicPantryRepository
import com.ala158.magicpantry.viewModel.ViewModelFactory

class ReviewIngredientsActivity : AppCompatActivity() {

    private lateinit var ingredientListView: ListView
    private lateinit var database: MagicPantryDatabase
    private lateinit var ingredientDAO: IngredientDAO
    private lateinit var repository: MagicPantryRepository
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var reviewIngredientsViewModel: ReviewIngredientsViewModel

    private lateinit var cancelButton: Button
    private lateinit var addAllButton: Button
    private lateinit var reviewIngredientsArrayAdapter: ReviewIngredientsActivityAdapter

    private lateinit var sharedPreferences : SharedPreferences

    private val ingredientList = arrayListOf<Ingredient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_review_ingredients)

        database = MagicPantryDatabase.getInstance(this)
        ingredientDAO = database.ingredientDAO
        repository = MagicPantryRepository(ingredientDAO)
        viewModelFactory = ViewModelFactory(repository)
        reviewIngredientsViewModel =
            ViewModelProvider(
                this,
                viewModelFactory
            ).get(ReviewIngredientsViewModel::class.java)

        val b = this.intent.extras
        val prod = b!!.getStringArray("arrayList")
        val prices = b.getStringArray("priceList")

        sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)

        if (prod != null && prices != null) {
            for (i in prod.indices) {
                val ingredient = Ingredient()

                if (prod[i].contains(";;")) {
                    val temp = prod[i].split(";;")
                    val quantity = temp[1]

                    ingredient.name = temp[0]
                    ingredient.amount = if (quantity.substring(0, 1) == "G") {
                        6
                    } else {
                        quantity.substring(0, 1).toInt()
                    }
                } else {
                    ingredient.name = prod[i]
                    ingredient.amount = 1
                }
                ingredient.price = prices[i].filter { it.isDigit() || it == '.' }.toDouble()

                ingredientList.add(ingredient)
                Log.d("ingredient", "$ingredient   and   $i")
            }
        }

        ingredientListView = findViewById(R.id.reviewIngredientsList)
        reviewIngredientsArrayAdapter = ReviewIngredientsActivityAdapter(this, ingredientList)
        ingredientListView.adapter = reviewIngredientsArrayAdapter

        cancelButton = findViewById(R.id.reviewCancelButton)
        addAllButton = findViewById(R.id.reviewAddAllButton)

        cancelButton.setOnClickListener {
            // TODO When receipt scanning API ready
            // requireActivity().supportFragmentManager.popBackStack()
            onBackPressed()
        }
        addAllButton.setOnClickListener {
            reviewIngredientsViewModel.addToIngredientList(ingredientList)
            reviewIngredientsViewModel.insertAll()
            Toast.makeText(this, "Items added!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val edit = sharedPreferences.edit()

        if (sharedPreferences.contains("name")) {
            val ingredient = Ingredient()
            ingredient.name = sharedPreferences.getString("name", "") as String
            ingredient.amount = sharedPreferences.getInt("amount", 0)
            ingredient.price = sharedPreferences.getString("price", "")!!.toDouble()
            ingredient.unit = sharedPreferences.getString("unit", "") as String

            ingredientList[sharedPreferences.getInt("currPos", 0)] = ingredient

            ingredientListView.adapter = null
            reviewIngredientsArrayAdapter = ReviewIngredientsActivityAdapter(this, ingredientList)
            ingredientListView.adapter = reviewIngredientsArrayAdapter
            reviewIngredientsArrayAdapter.notifyDataSetChanged()
        }
        edit.remove("name")
        edit.remove("currPos")
        edit.remove("amount")
        edit.remove("price")
        edit.remove("unit")
        edit.apply()
    }
}