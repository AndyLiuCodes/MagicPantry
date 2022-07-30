package com.ala158.magicpantry.arrayAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.ShoppingListItemAndIngredient


class ShoppingListArrayAdapter(
            private val context: Context,
            private var shoppingListItemAndIngredient: List<ShoppingListItemAndIngredient>) : BaseAdapter() {

    override fun getCount(): Int {
        return shoppingListItemAndIngredient.size
    }

    override fun getItem(position: Int): Any {
        return shoppingListItemAndIngredient[position].shoppingListItem
    }

    override fun getItemId(position: Int): Long {
        return shoppingListItemAndIngredient[position].shoppingListItem.shoppingListItemId
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.list_item_shopping_list, null)
        val amountTextView = view.findViewById<TextView>(R.id.shopping_list_item_amount)
        val unitTextView = view.findViewById<TextView>(R.id.shopping_list_item_unit)
        val nameTextView = view.findViewById<TextView>(R.id.shopping_list_item_name)
        val isBoughtCheckbox = view.findViewById<CheckBox>(R.id.shopping_list_checkbox_purchased)

        val shoppingListItem = shoppingListItemAndIngredient[position].shoppingListItem
        amountTextView.text = shoppingListItem.itemAmount.toString()
        unitTextView.text = shoppingListItem.itemUnit
        nameTextView.text = shoppingListItem.itemName

        isBoughtCheckbox.isChecked = shoppingListItem.isItemBought

        return view
    }

    fun replace(newShoppingListItemAndIngredient: List<ShoppingListItemAndIngredient>) {
        shoppingListItemAndIngredient = newShoppingListItemAndIngredient
    }
}