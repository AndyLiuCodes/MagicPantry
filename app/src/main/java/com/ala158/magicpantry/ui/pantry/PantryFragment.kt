package com.ala158.magicpantry.ui.pantry

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.ala158.magicpantry.R
import com.ala158.magicpantry.arrayAdapter.PantryIngredientsArrayAdapter
import com.ala158.magicpantry.dao.IngredientDAO
import com.ala158.magicpantry.database.MagicPantryDatabase
import com.ala158.magicpantry.repository.MagicPantryRepository
import com.ala158.magicpantry.ui.manualingredientinput.ManualIngredientInputFragment
import com.ala158.magicpantry.viewModel.ViewModelFactory

class PantryFragment : Fragment(), CompoundButton.OnCheckedChangeListener {
    private lateinit var pantryViewModel: PantryViewModel
    private lateinit var pantryViewModelFactory: ViewModelFactory
    private lateinit var magicPantryDatabase: MagicPantryDatabase
    private lateinit var ingredientDAO: IngredientDAO
    private lateinit var magicPantryRepository: MagicPantryRepository
    private lateinit var allIngredientsListView: ListView
    private lateinit var pantryIngredientsArrayAdapter: PantryIngredientsArrayAdapter
    private lateinit var btnAddIngredient: Button
    private lateinit var filterLowStockCheckbox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        magicPantryDatabase = MagicPantryDatabase.getInstance(requireActivity())
        ingredientDAO = magicPantryDatabase.ingredientDAO
        magicPantryRepository = MagicPantryRepository(ingredientDAO)
        pantryViewModelFactory = ViewModelFactory(magicPantryRepository)
        pantryViewModel = ViewModelProvider(this, pantryViewModelFactory)
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
        allIngredientsListView = view.findViewById(R.id.listview_pantry_all_ingredients)
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

        btnAddIngredient.setOnClickListener {
            view.findNavController().navigate(R.id.navigation_manual_ingredient_input)
        }

        filterLowStockCheckbox.setOnCheckedChangeListener(this)
        return view
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (filterLowStockCheckbox.isChecked) {
            pantryIngredientsArrayAdapter.replace(pantryViewModel.lowStockIngredients.value!!)
        } else {
            pantryIngredientsArrayAdapter.replace(pantryViewModel.allIngredientsLiveData.value!!)
        }
        pantryIngredientsArrayAdapter.notifyDataSetChanged()
    }
}