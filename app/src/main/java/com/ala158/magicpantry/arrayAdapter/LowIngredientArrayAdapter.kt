package com.ala158.magicpantry.arrayAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.NotificationWithIngredients
import java.util.*

class LowIngredientArrayAdapter(
    private val context: Context,
    private var lowIngredientList: List<Ingredient>
) : BaseAdapter() {
    // Only show ingredients that have a pantry amount < notify threshold amount
    var validIngredients: List<Ingredient> = ArrayList()

    override fun getCount(): Int {
        return validIngredients.size
    }

    override fun getItem(position: Int): Any {
        return validIngredients[position]
    }

    override fun getItemId(position: Int): Long {
        return validIngredients[position].ingredientId
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(context, R.layout.list_item_low_ingredient_list, null)

        val itemName = view.findViewById<TextView>(R.id.low_ingredient_list_item_name)
        val addBtn = view.findViewById<ImageButton>(R.id.low_ingredient_list_item_add_button)

        val ingredient = validIngredients[position]

        addBtn.setOnClickListener {
            //TODO: Add item to shopping list
            Toast.makeText(view.context, "Added", Toast.LENGTH_SHORT).show()
        }

        itemName.text = ingredient.name

        return view
    }

    fun replace(newIngredients: List<Ingredient>) {
        lowIngredientList = newIngredients
        updateValidIngredients()
    }

    private fun updateValidIngredients() {
        validIngredients = lowIngredientList.filter { it.amount < it.notifyThreshold }
        notifyDataSetChanged()
    }
}