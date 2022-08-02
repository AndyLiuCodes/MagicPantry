package com.ala158.magicpantry.ui.shoppinglist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.ShoppingListArrayAdapter
import com.ala158.magicpantry.data.ShoppingListItem
import com.ala158.magicpantry.data.ShoppingListItemAndIngredient
import com.ala158.magicpantry.database.MagicPantryDatabase
import com.ala158.magicpantry.dialogs.ShoppingListChangeAmountDialog
import com.ala158.magicpantry.repository.ShoppingListItemRepository
import com.ala158.magicpantry.ui.ingredientlistadd.IngredientListAddActivity
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.ShoppingListItemViewModel

class ShoppingListFragment : Fragment(), ShoppingListArrayAdapter.OnChangeShoppingItemAmountClickListener {
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
            requireActivity(),
            ArrayList(),
            shoppingListItemRepository,
            this
        )
        shoppingListListView.adapter = shoppingListArrayAdapter

        shoppingListItemViewModel.allShoppingListItemsLiveData.observe(requireActivity()) {
            shoppingListArrayAdapter.replace(it)
            shoppingListArrayAdapter.notifyDataSetChanged()
        }

        addIngredientsButton = view.findViewById(R.id.btn_add_shopping_list_item)

        addIngredientsButton.setOnClickListener {
            val intent = Intent(requireActivity(), IngredientListAddActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(Util.INGREDIENT_ADD_LIST, Util.INGREDIENT_ADD_SHOPPING_LIST)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        return view
    }

    override fun onChangeShoppingItemAmountClick(shoppingListItemAndIngredient: ShoppingListItemAndIngredient) {
        val shoppingListChangeAmountDialog = ShoppingListChangeAmountDialog()

        val dialogData = Bundle()
        val pantryIngredient = shoppingListItemAndIngredient.ingredient
        val shoppingListItem = shoppingListItemAndIngredient.shoppingListItem
        dialogData.putString(DIALOG_INGREDIENT_NAME_KEY, pantryIngredient.name)
        dialogData.putString(DIALOG_INGREDIENT_UNIT_KEY, pantryIngredient.unit)

        val savedShoppingListItemMessageListener = SavedShoppingListItemMessageListener(requireActivity())
        dialogData.putParcelable(DIALOG_SHOPPING_LIST_LISTENER_KEY, object : ShoppingListChangeAmountDialog.ShoppingListChangeAmountDialogListener {
            override fun onShoppingListChangeAmountDialogClick(amount: Double) {
                shoppingListItem.itemAmount = amount
                shoppingListItemViewModel.update(shoppingListItem)
                savedShoppingListItemMessageListener.savedSuccessfullyMessage()
            }

            override fun describeContents(): Int {
                return 0
            }

            override fun writeToParcel(dest: Parcel?, flags: Int) {
                return
            }
        })

        shoppingListChangeAmountDialog.arguments = dialogData
        shoppingListChangeAmountDialog.show(parentFragmentManager, "Change shopping list item amount")
    }

    internal class SavedShoppingListItemMessageListener(private val context: Context) {
        fun savedSuccessfullyMessage() {
            Toast.makeText(context, "New amount saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!requireActivity().isChangingConfigurations)
            shoppingListItemViewModel.deleteAlldeleteAllIsBoughtShoppingListItems()
    }

    companion object {
        const val DIALOG_INGREDIENT_NAME_KEY = "DIALOG_INGREDIENT_NAME_KEY"
        const val DIALOG_INGREDIENT_UNIT_KEY = "DIALOG_INGREDIENT_UNIT_KEY"
        const val DIALOG_SHOPPING_LIST_LISTENER_KEY = "DIALOG_SHOPPING_LIST_LISTENER_KEY"
    }
}