package com.ala158.magicpantry.ui.pantry

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ala158.magicpantry.R
import com.ala158.magicpantry.arrayAdapter.PantryIngredientsArrayAdapter
import com.ala158.magicpantry.dao.IngredientDAO
import com.ala158.magicpantry.database.MagicPantryDatabase
import com.ala158.magicpantry.repository.IngredientRepository
import com.ala158.magicpantry.ui.manualingredientinput.ManualIngredientInputActivity
import com.ala158.magicpantry.ui.manualingredientinput.edit.PantryEditIngredientActivity
import com.ala158.magicpantry.ui.receiptscanner.ReceiptScannerActivity
import com.ala158.magicpantry.viewModel.IngredientViewModelFactory

class PantryFragment : Fragment(), CompoundButton.OnCheckedChangeListener {
    private lateinit var pantryViewModel: PantryViewModel
    private lateinit var pantryIngredientViewModelFactory: IngredientViewModelFactory
    private lateinit var magicPantryDatabase: MagicPantryDatabase
    private lateinit var ingredientDAO: IngredientDAO
    private lateinit var ingredientRepository: IngredientRepository
    private lateinit var allIngredientsListView: ListView
    private lateinit var pantryIngredientsArrayAdapter: PantryIngredientsArrayAdapter
    private lateinit var btnAddIngredient: Button
    private lateinit var btnScanReceipt: Button
    private lateinit var filterLowStockCheckbox: CheckBox
    private lateinit var textViewPantryHeader: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        magicPantryDatabase = MagicPantryDatabase.getInstance(requireActivity())
        ingredientDAO = magicPantryDatabase.ingredientDAO
        ingredientRepository = IngredientRepository(ingredientDAO)
        pantryIngredientViewModelFactory = IngredientViewModelFactory(ingredientRepository)
        pantryViewModel = ViewModelProvider(this, pantryIngredientViewModelFactory)
            .get(PantryViewModel::class.java)
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

        pantryIngredientsArrayAdapter =
            PantryIngredientsArrayAdapter(requireActivity(), ArrayList())
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

        allIngredientsListView.setOnItemClickListener {
            _, _, _, id ->

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

    companion object {
        val PANTRY_INGREDIENT_ID_KEY = "PANTRY_INGREDIENT_ID_KEY"
    }
}