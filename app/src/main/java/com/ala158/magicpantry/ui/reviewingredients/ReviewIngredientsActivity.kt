package com.ala158.magicpantry.ui.reviewingredients

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_review_ingredients)
        // Inflate the layout for this fragment

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
        val array = b!!.getStringArray("arrayList")
        val prices = b.getStringArray("priceList")

        val ingredientList = arrayListOf<Ingredient>()

//        Log.d("ingredient", "$array")

        if (array != null) {
            for (i in array.indices) {
                val ingredient = Ingredient()

                if (array[i].contains(";;")) {
                    val temp = array[i].split(";;")
                    val quantity = temp[1]

                    ingredient.name = temp[0]
                    ingredient.amount = if (quantity.substring(0, 1) == "G") {
                        6
                    } else {
                        quantity.substring(0, 1).toInt()
                    }
                } else {
                    ingredient.name = array[i]
                    ingredient.amount = 1
                }
                ingredient.price = prices!![i].filter { it.isDigit() || it == '.' }.toDouble()

                ingredientList.add(ingredient)
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
            reviewIngredientsViewModel.insertAll()
            Toast.makeText(this, "Items added!", Toast.LENGTH_SHORT).show()
        }

    }
}