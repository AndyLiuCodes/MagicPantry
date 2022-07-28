package com.ala158.magicpantry.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ala158.magicpantry.MockData
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.NotificationsListArrayAdapter
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.NotificationViewModel

class NotificationsFragment : Fragment() {
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var ingredientViewModel: IngredientViewModel

    private lateinit var notificationsListView: ListView
    private lateinit var notificationsListArrayAdapter: NotificationsListArrayAdapter

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

        notificationsListArrayAdapter =
            NotificationsListArrayAdapter(requireActivity(), MockData.notifications)

        notificationsListView = view.findViewById(R.id.notifications_list_view)
        notificationsListView.adapter = notificationsListArrayAdapter

        // TODO: add back in when notifications are generated
/*
        notificationViewModel.allNotifications.observe(requireActivity()) {
            notificationsListArrayAdapter.replace(it)
            notificationsListArrayAdapter.notifyDataSetChanged()
        }
*/

        return view
    }
}