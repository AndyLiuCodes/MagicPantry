package com.ala158.magicpantry.ui.pantry

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ala158.magicpantry.MockData
import com.ala158.magicpantry.R
import com.ala158.magicpantry.arrayAdapter.IngredientsArrayAdapter
import com.ala158.magicpantry.arrayAdapter.PantryIngredientsArrayAdapter
import com.ala158.magicpantry.dao.IngredientDAO
import com.ala158.magicpantry.database.MagicPantryDatabase
import com.ala158.magicpantry.repository.MagicPantryRepository
import com.ala158.magicpantry.ui.manualingredientinput.ManualIngredientInputActivity
import com.ala158.magicpantry.viewModel.ViewModelFactory

class PantryFragment : Fragment() {
    private lateinit var pantryViewModel: PantryViewModel
    private lateinit var pantryViewModelFactory: ViewModelFactory
    private lateinit var magicPantryDatabase: MagicPantryDatabase
    private lateinit var ingredientDAO: IngredientDAO
    private lateinit var magicPantryRepository: MagicPantryRepository
    private lateinit var allIngredientsListView: ListView
    private lateinit var pantryIngredientsArrayAdapter: PantryIngredientsArrayAdapter

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


        allIngredientsListView = view.findViewById(R.id.listview_pantry_all_ingredients)
        pantryIngredientsArrayAdapter =
            PantryIngredientsArrayAdapter(requireActivity(), ArrayList())
        allIngredientsListView.adapter = pantryIngredientsArrayAdapter

        pantryViewModel.allIngredientsLiveData.observe(requireActivity()) {
            pantryIngredientsArrayAdapter.replace(it)
            pantryIngredientsArrayAdapter.notifyDataSetChanged()
        }

        val btnAddIngredient = view.findViewById<Button>(R.id.btn_add_ingredient)
        btnAddIngredient.setOnClickListener {
            addIngredientManually()
        }
        return view
    }

    private fun addIngredientManually() {
        val intent = Intent(activity, ManualIngredientInputActivity::class.java)
        startActivity(intent)
    }

}