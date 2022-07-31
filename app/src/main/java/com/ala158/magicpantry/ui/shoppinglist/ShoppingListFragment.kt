package com.ala158.magicpantry.ui.shoppinglist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.ShoppingListArrayAdapter
import com.ala158.magicpantry.database.MagicPantryDatabase
import com.ala158.magicpantry.repository.ShoppingListItemRepository
import com.ala158.magicpantry.ui.ingredientlistadd.IngredientListAddActivity
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.ShoppingListItemViewModel

class ShoppingListFragment : Fragment() {
    private lateinit var shoppingListItemViewModel: ShoppingListItemViewModel
    private lateinit var ingredientViewModel: IngredientViewModel
    private lateinit var shoppingListListView: ListView
    private lateinit var shoppingListArrayAdapter: ShoppingListArrayAdapter
    private lateinit var database: MagicPantryDatabase
    private lateinit var shoppingListItemRepository: ShoppingListItemRepository

    private lateinit var addIngredientsButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        database = MagicPantryDatabase.getInstance(requireActivity())
        shoppingListItemRepository = ShoppingListItemRepository(database.shoppingListItemDAO)

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

        shoppingListArrayAdapter = ShoppingListArrayAdapter(
            requireActivity(), ArrayList(), shoppingListItemRepository
        )
        shoppingListListView.adapter = shoppingListArrayAdapter

        shoppingListItemViewModel.allShoppingListItemsLiveData.observe(requireActivity()) {
            shoppingListArrayAdapter.replace(it)
            shoppingListArrayAdapter.notifyDataSetChanged()
        }

        addIngredientsButton = view.findViewById(R.id.btn_add_shopping_list_item)

        addIngredientsButton.setOnClickListener {
            val intent = Intent(requireActivity(), IngredientListAddActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!requireActivity().isChangingConfigurations)
            shoppingListItemViewModel.deleteAlldeleteAllIsBoughtShoppingListItems()
    }
}