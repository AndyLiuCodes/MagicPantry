package com.ala158.magicpantry.arrayAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.ShoppingListItem


class ShoppingListArrayAdapter(
            private val context: Context,
            private var shoppingList: List<ShoppingListItem>) : BaseAdapter() {

    override fun getCount(): Int {
        return shoppingList.size
    }

    override fun getItem(position: Int): Any {
        return shoppingList[position]
    }

    override fun getItemId(position: Int): Long {
        return shoppingList[position].shoppingListItemId
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.list_item_shopping_list, null)
        val amountTextView = view.findViewById<TextView>(R.id.shopping_list_item_amount)
        val unitTextView = view.findViewById<TextView>(R.id.shopping_list_item_unit)
        val nameTextView = view.findViewById<TextView>(R.id.shopping_list_item_name)
        val isBoughtCheckbox = view.findViewById<CheckBox>(R.id.shopping_list_checkbox_purchased)

        val shoppingListItem = shoppingList[position]
        amountTextView.text = shoppingListItem.itemAmount.toString()
        unitTextView.text = shoppingListItem.itemUnit
        nameTextView.text = shoppingListItem.itemName

        isBoughtCheckbox.isChecked = shoppingListItem.isItemBought

        return view
    }

    fun replace(newShoppingList: List<ShoppingListItem>) {
        shoppingList = newShoppingList.toList()
    }
}