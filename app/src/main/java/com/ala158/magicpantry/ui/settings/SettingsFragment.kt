package com.ala158.magicpantry.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ala158.magicpantry.R

class SettingsFragment : Fragment() {
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        return view
    }
}