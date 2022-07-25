package com.ala158.magicpantry.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.NotificationViewModel

class NotificationsFragment : Fragment() {
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var ingredientViewModel: IngredientViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        notificationViewModel = Util.createViewModel(
            requireActivity(),
            NotificationViewModel::class.java,
            Util.DataType.NOTIFICATION
        )

        ingredientViewModel = Util.createViewModel(
            requireActivity(),
            IngredientViewModel::class.java,
            Util.DataType.INGREDIENT
        )

        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        val textView: TextView = view.findViewById(R.id.text_notification)
        notificationViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return view
    }
}