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

    override fun getCount(): Int {
        return lowIngredientList.size
    }

    override fun getItem(position: Int): Any {
        return lowIngredientList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(context, R.layout.list_item_low_ingredient_list, null)

        val itemName = view.findViewById<TextView>(R.id.low_ingredient_list_item_name)
        val deleteBtn = view.findViewById<ImageButton>(R.id.low_ingredient_list_item_delete_button)

        val selectedNotification = lowIngredientList[position]

        deleteBtn.setOnClickListener {
            //TODO: delete item from list
            Toast.makeText(view.context, "Added", Toast.LENGTH_SHORT).show()
        }

        itemName.text = selectedNotification.name

        return view
    }

    fun replace(newIngredient:List<Ingredient>){
        lowIngredientList = newIngredient
    }
}