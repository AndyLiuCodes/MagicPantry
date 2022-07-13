package com.ala158.magicpantry.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ala158.magicpantry.R

class RecipesFragment : Fragment() {
    private lateinit var recipesViewModel: RecipesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recipesViewModel = ViewModelProvider(this).get(RecipesViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_recipes, container, false)

        val textView: TextView = view.findViewById(R.id.text_recipes)
        recipesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return view
    }
}