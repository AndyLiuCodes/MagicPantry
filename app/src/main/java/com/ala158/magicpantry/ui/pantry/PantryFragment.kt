package com.ala158.magicpantry.ui.pantry

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.PantryIngredientsArrayAdapter
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.ShoppingListItem
import com.ala158.magicpantry.database.MagicPantryDatabase
import com.ala158.magicpantry.dialogs.PantryAddShoppingListDialog
import com.ala158.magicpantry.repository.ShoppingListItemRepository
import com.ala158.magicpantry.ui.manualingredientinput.ManualIngredientInputActivity
import com.ala158.magicpantry.ui.manualingredientinput.edit.PantryEditIngredientActivity
import com.ala158.magicpantry.ui.receiptscanner.ReceiptScannerActivity

class PantryFragment : Fragment(),
    CompoundButton.OnCheckedChangeListener,
    PantryIngredientsArrayAdapter.OnItemAddToShoppingClickListener {
    private lateinit var pantryViewModel: PantryViewModel
    private lateinit var allIngredientsListView: ListView
    private lateinit var pantryIngredientsArrayAdapter: PantryIngredientsArrayAdapter
    private lateinit var btnAddIngredient: Button
    private lateinit var btnScanReceipt: Button
    private lateinit var filterLowStockCheckbox: CheckBox
    private lateinit var textViewPantryHeader: TextView
    private lateinit var database: MagicPantryDatabase
    private lateinit var shoppingListItemRepository: ShoppingListItemRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantryViewModel =
            Util.createViewModel(
                requireActivity(),
                PantryViewModel::class.java,
                Util.DataType.INGREDIENT
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_pantry, container, false)
        filterLowStockCheckbox = view.findViewById(R.id.checkbox_filter_low_stock)
        btnAddIngredient = view.findViewById(R.id.btn_add_ingredient)
        btnScanReceipt = view.findViewById(R.id.btn_scan_receipt)
        allIngredientsListView = view.findViewById(R.id.listview_pantry_all_ingredients)
        textViewPantryHeader = view.findViewById(R.id.header_pantry)

        database = MagicPantryDatabase.getInstance(requireActivity())
        shoppingListItemRepository = ShoppingListItemRepository(database.shoppingListItemDAO)

        pantryIngredientsArrayAdapter = PantryIngredientsArrayAdapter(
            requireActivity(),
            ArrayList(),
            this
        )

        allIngredientsListView.adapter = pantryIngredientsArrayAdapter

        pantryViewModel.allIngredientsLiveData.observe(requireActivity()) {
            pantryViewModel.updateLowStockList()
            // Update listview with the new all ingredients if filter low stock is not checked.
            // If it is checked, the updateLowStock will update the mutablelivedata, which
            // will trigger a listview update with lowstock
            if (!filterLowStockCheckbox.isChecked) {
                pantryIngredientsArrayAdapter.replace(it)
                pantryIngredientsArrayAdapter.notifyDataSetChanged()
            }
        }

        pantryViewModel.lowStockIngredients.observe(requireActivity()) {
            if (filterLowStockCheckbox.isChecked) {
                pantryIngredientsArrayAdapter.replace(it)
                pantryIngredientsArrayAdapter.notifyDataSetChanged()
            }
        }

        allIngredientsListView.setOnItemClickListener { _, _, _, id ->

            val intent = Intent(requireActivity(), PantryEditIngredientActivity::class.java)
            intent.putExtra(PANTRY_INGREDIENT_ID_KEY, id)
            startActivity(intent)
        }

        btnAddIngredient.setOnClickListener {
            val intent = Intent(activity, ManualIngredientInputActivity::class.java)
            startActivity(intent)
        }

        btnScanReceipt.setOnClickListener {
            val intent = Intent(activity, ReceiptScannerActivity::class.java)
            startActivity(intent)
        }

        filterLowStockCheckbox.setOnCheckedChangeListener(this)

        return view
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (filterLowStockCheckbox.isChecked) {
            pantryIngredientsArrayAdapter.replace(pantryViewModel.lowStockIngredients.value!!)
            setHeaderLowStock()
        } else {
            pantryIngredientsArrayAdapter.replace(pantryViewModel.allIngredientsLiveData.value!!)
            setHeaderAllIngredients()
        }
        pantryIngredientsArrayAdapter.notifyDataSetChanged()
    }

    private fun setHeaderAllIngredients() {
        textViewPantryHeader.setBackgroundResource(R.drawable.rounded_bg_blue)
        textViewPantryHeader.setText(R.string.all_ingredients_header)
    }

    private fun setHeaderLowStock() {
        textViewPantryHeader.setBackgroundResource(R.drawable.rounded_bg_red)
        textViewPantryHeader.setText(R.string.low_stock_header)
    }

    override fun onItemAddToShoppingListClick(ingredient: Ingredient) {
        val pantryAddShoppingListDialog = PantryAddShoppingListDialog()
        // https://stackoverflow.com/questions/56543161/how-to-pass-arguments-to-dialog-in-android
        // for passing arguments into a dialog
        val dialogData = Bundle()
        dialogData.putString(DIALOG_RELATED_INGREDIENT_NAME_KEY, ingredient.name)
        dialogData.putString(DIALOG_RELATED_INGREDIENT_UNIT_KEY, ingredient.unit)
        dialogData.putLong(DIALOG_RELATED_INGREDIENT_ID_KEY, ingredient.ingredientId)

        // Had help from https://stackoverflow.com/a/57789419 to pass a listener object to a DialogFragment
        // as a parcelable
        val addedToShoppingListToastMessageListener =
            AddedToShoppingListToastMessageListener(requireActivity())
        dialogData.putParcelable(
            DIALOG_ADD_SHOPPING_LIST_LISTENER_KEY,
            object : PantryAddShoppingListDialog.PantryAddShoppingListDialogListener {
                private val addedToShoppingListListener = addedToShoppingListToastMessageListener

                override fun onPantryAddShoppingListDialogClick(
                    unit: String, name: String, id: Long, amount: Double
                ) {
                    if (amount == 0.0) {
                        addedToShoppingListListener.nothingAddedMessage()
                    } else {
                        // Add item to database
                        val shoppingListItem = ShoppingListItem(
                            amount,
                            false
                        )
                        shoppingListItem.relatedIngredientId = id
                        Log.d(
                            "PANTRY",
                            "onPantryAddShoppingListDialogClick: shopping list item $shoppingListItem"
                        )

                        shoppingListItemRepository.insertShoppingListItemFromPantry(shoppingListItem)

                        addedToShoppingListListener.addedSuccessfullyMessage(amount, unit, name)
                    }
                }

                override fun describeContents(): Int {
                    return 0
                }

                override fun writeToParcel(dest: Parcel?, flags: Int) {
                    return
                }
            })
        pantryAddShoppingListDialog.arguments = dialogData
        pantryAddShoppingListDialog.show(parentFragmentManager, "Add to Shopping List")

    }

    // Used to print the toast messages after shoppinglistitem added
    // Need a separate class as the Dialog loses fragment context on orientation change
    // Used in onItemAddToShoppingListClick
    internal class AddedToShoppingListToastMessageListener(private val context: Context) {
        fun nothingAddedMessage() {
            Toast.makeText(
                context,
                "No items were added to shopping list!",
                Toast.LENGTH_SHORT
            ).show()
        }

        fun addedSuccessfullyMessage(amount: Double, unit: String, name: String) {
            Toast.makeText(
                context,
                "Added $amount $unit of $name to your shopping list!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val PANTRY_INGREDIENT_ID_KEY = "PANTRY_INGREDIENT_ID_KEY"
        const val DIALOG_RELATED_INGREDIENT_NAME_KEY = "DIALOG_RELATED_INGREDIENT_NAME_KEY"
        const val DIALOG_RELATED_INGREDIENT_UNIT_KEY = "DIALOG_RELATED_INGREDIENT_UNIT_KEY"
        const val DIALOG_RELATED_INGREDIENT_ID_KEY = "DIALOG_RELATED_INGREDIENT_ID_KEY"
        const val DIALOG_ADD_SHOPPING_LIST_LISTENER_KEY = "DIALOG_ADD_SHOPPING_LIST_LISTENER_KEY"
    }
}