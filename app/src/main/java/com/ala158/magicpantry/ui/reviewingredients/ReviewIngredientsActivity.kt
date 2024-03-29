package com.ala158.magicpantry.ui.reviewingredients

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.ReviewIngredientsActivityAdapter
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.ui.manualingredientinput.edit.ReviewIngredientsEditActivity

class ReviewIngredientsActivity : AppCompatActivity() {

    private lateinit var ingredientListView: ListView
    private lateinit var reviewIngredientsViewModel: ReviewIngredientsViewModel

    private lateinit var cancelButton: Button
    private lateinit var addAllButton: Button
    private lateinit var reviewIngredientsArrayAdapter: ReviewIngredientsActivityAdapter

    private lateinit var sharedPreferences: SharedPreferences

    private val ingredientList = arrayListOf<Ingredient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_review_ingredients)

        reviewIngredientsViewModel = Util.createViewModel(
            this,
            ReviewIngredientsViewModel::class.java,
            Util.DataType.INGREDIENT
        )

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
                        6.0
                    } else {
                        quantity.substring(0, 1).toDouble()
                    }
                } else {
                    ingredient.name = prod[i]
                    ingredient.amount = 1.0
                }
                ingredient.price = prices[i].filter { it.isDigit() || it == '.' }.toDouble()

                ingredientList.add(ingredient)
            }
        }

        ingredientListView = findViewById(R.id.reviewIngredientsList)
        reviewIngredientsArrayAdapter = ReviewIngredientsActivityAdapter(this, ingredientList)
        ingredientListView.adapter = reviewIngredientsArrayAdapter
        reviewIngredientsViewModel.ingredientList.observe(this) {
            reviewIngredientsArrayAdapter.replace(it)
            reviewIngredientsArrayAdapter.notifyDataSetChanged()
        }
        cancelButton = findViewById(R.id.reviewCancelButton)
        addAllButton = findViewById(R.id.reviewAddAllButton)

        cancelButton.setOnClickListener {
            finish()
        }
        addAllButton.setOnClickListener {
            reviewIngredientsViewModel.ingredientList.value = ingredientList
            reviewIngredientsViewModel.insertAll()
            Toast.makeText(this, "Items added!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val edit = sharedPreferences.edit()

        if (sharedPreferences.contains(ReviewIngredientsEditActivity.CURRENT_POSITION_KEY)) {
            val ingredient = Ingredient()
            val position = sharedPreferences.getInt(
                ReviewIngredientsEditActivity.CURRENT_POSITION_KEY, 0
            )
            val isDelete = sharedPreferences.getBoolean(
                ReviewIngredientsEditActivity.DELETE_INGREDIENT_KEY,
                false
            )
            if (isDelete) {
                ingredientList.removeAt(position)
                reviewIngredientsViewModel.ingredientList.value = ingredientList
                Toast.makeText(this, "Deleted ingredient!", Toast.LENGTH_SHORT).show()
            } else {
                val name = sharedPreferences.getString(ReviewIngredientsEditActivity.NAME_KEY, "")
                val amountString =
                    sharedPreferences.getString(ReviewIngredientsEditActivity.AMOUNT_KEY, "")
                val priceString =
                    sharedPreferences.getString(ReviewIngredientsEditActivity.PRICE_KEY, "")
                val unit =
                    sharedPreferences.getString(ReviewIngredientsEditActivity.UNIT_KEY, "unit")
                val isNotify =
                    sharedPreferences.getBoolean(ReviewIngredientsEditActivity.IS_NOTIFY_KEY, false)
                val notifyThresholdString = sharedPreferences.getString(
                    ReviewIngredientsEditActivity.NOTIFY_THRESHOLD_KEY,
                    "0.0"
                )

                if (name != null || name != "") {
                    ingredient.name = name!!
                }

                if (amountString != null || amountString != "") {
                    ingredient.amount = amountString!!.toDouble()
                }

                if (priceString != null || priceString != "") {
                    ingredient.price = priceString!!.toDouble()
                }

                if (unit != null) {
                    ingredient.unit = unit
                }

                if (isNotify != null) {
                    ingredient.isNotify = isNotify
                }

                if (notifyThresholdString != null || notifyThresholdString != "") {
                    ingredient.notifyThreshold = notifyThresholdString!!.toDouble()
                }

                ingredientList[position] = ingredient
                reviewIngredientsViewModel.ingredientList.value = ingredientList
            }
        }

        edit.remove(ReviewIngredientsEditActivity.DELETE_INGREDIENT_KEY)
        edit.remove(ReviewIngredientsEditActivity.NAME_KEY)
        edit.remove(ReviewIngredientsEditActivity.CURRENT_POSITION_KEY)
        edit.remove(ReviewIngredientsEditActivity.AMOUNT_KEY)
        edit.remove(ReviewIngredientsEditActivity.PRICE_KEY)
        edit.remove(ReviewIngredientsEditActivity.UNIT_KEY)
        edit.remove(ReviewIngredientsEditActivity.IS_NOTIFY_KEY)
        edit.remove(ReviewIngredientsEditActivity.NOTIFY_THRESHOLD_KEY)

        edit.apply()
    }
}