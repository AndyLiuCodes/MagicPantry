package com.ala158.magicpantry.ui.manualingredientinput

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ala158.magicpantry.R

class ManualIngredientInputFragment : Fragment() {
    private lateinit var btnAddToPantry: Button
    private lateinit var btnCancel: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_manual_ingredient_input, container, false)
        btnAddToPantry = view.findViewById(R.id.btn_add_to_pantry)
        btnCancel = view.findViewById(R.id.btn_cancel_pantry)

        btnAddToPantry.setOnClickListener {
            addItemToPantry()
        }

        btnCancel.setOnClickListener {
            view.findNavController().navigate(R.id.navigation_pantry)
        }
        return view
    }

    private fun addItemToPantry() {
        // TODO
        Log.d("ADD ITEM", "addItemToPantry: ")
    }
}