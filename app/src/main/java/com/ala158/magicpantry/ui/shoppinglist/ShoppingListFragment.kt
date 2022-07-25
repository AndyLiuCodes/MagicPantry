package com.ala158.magicpantry.ui.shoppinglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ala158.magicpantry.MockData
import com.ala158.magicpantry.R
import com.ala158.magicpantry.arrayAdapter.ShoppingListArrayAdapter

class ShoppingListFragment : Fragment() {
    private lateinit var shoppingListItemViewModel: ShoppingListItemViewModel
    private lateinit var ingredientViewModel: IngredientViewModel
    private lateinit var shoppingListListView: ListView
    private lateinit var shoppingListArrayAdapter: ShoppingListArrayAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        shoppingListItemViewModel = Util.createViewModel(
            requireActivity(),
            ShoppingListItemViewModel::class.java,
            Util.DataType.SHOPPING_LIST_ITEM
        )

        ingredientViewModel = Util.createViewModel(
            requireActivity(),
            IngredientViewModel::class.java,
            Util.DataType.INGREDIENT
        )

        val view = inflater.inflate(R.layout.fragment_shopping_list, container, false)
        shoppingListListView = view.findViewById(R.id.listview_shopping_list_items)

        shoppingListArrayAdapter = ShoppingListArrayAdapter(requireActivity(), MockData.shoppingList)
        shoppingListListView.adapter = shoppingListArrayAdapter

        return view
    }
}