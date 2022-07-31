package com.ala158.magicpantry.arrayAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.NotificationWithIngredients
import java.util.*

class LowIngredientArrayAdapter(
    private val context: Context,
    private var lowIngredientList: Array<String>
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
        val deleteBtn = view.findViewById<TextView>(R.id.low_ingredient_list_item_delete_button)

        val selectedNotification = lowIngredientList[position]

        deleteBtn.setOnClickListener {
            //TODO: delete item from list
        }

        itemName.text = selectedNotification

        return view
    }
}