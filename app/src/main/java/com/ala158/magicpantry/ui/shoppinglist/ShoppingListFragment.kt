package com.ala158.magicpantry.ui.shoppinglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ala158.magicpantry.R

class ShoppingListFragment : Fragment() {
    private lateinit var shoppingListViewModel: ShoppingListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        shoppingListViewModel = ViewModelProvider(this).get(ShoppingListViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_shopping_list, container, false)

        val textView: TextView = view.findViewById(R.id.navigation_shopping_list)
        shoppingListViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return view
    }
}