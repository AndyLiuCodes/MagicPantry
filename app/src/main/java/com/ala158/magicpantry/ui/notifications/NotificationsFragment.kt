package com.ala158.magicpantry.ui.notifications

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.ala158.magicpantry.MockData
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.Notification
import com.ala158.magicpantry.data.NotificationWithIngredients
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.NotificationViewModel

class NotificationsFragment : Fragment() {
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var ingredientViewModel: IngredientViewModel

    private lateinit var addIngredientsButton: Button
    private lateinit var createNotificationButton: Button
    private lateinit var deleteNotificationButton: Button

    private lateinit var notifications: List<NotificationWithIngredients>
    private lateinit var ingredients: List<Ingredient>

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

        addIngredientsButton = view.findViewById(R.id.addIngredientsForNotifications)
        createNotificationButton = view.findViewById(R.id.addNotification)
        deleteNotificationButton = view.findViewById(R.id.deleteNotification)

        notificationViewModel.allNotifications.observe(viewLifecycleOwner) {
            Log.d("ITEM", "onCreateView: Num: ${it.size} Notifications: $it")
            notifications = it
        }

        ingredientViewModel.allIngredientsLiveData.observe(viewLifecycleOwner) {
            Log.d("ITEM", "onCreateView: Num: ${it.size} Ingredients: $it")
            ingredients = it
        }

        addIngredientsButton.setOnClickListener {
            Log.d("NOTIFY", "onCreateView: add ingredients")
            for (ingredient in MockData.allIngredientsToastTest) {
                ingredientViewModel.insert(ingredient)
            }
        }

        createNotificationButton.setOnClickListener {
            Log.d("NOTIFY", "onCreateView: create notification")
            notificationViewModel.insert(MockData.notification)
            val notificationId = notificationViewModel.newNotificationId.value!! + 1
            Log.d("NOTIFY", "onCreateView: notificationId: $notificationId")

            notificationViewModel.insertCrossRef(notificationId, ingredients[0].ingredientId)
            notificationViewModel.insertCrossRef(notificationId, ingredients[1].ingredientId)
        }

        deleteNotificationButton.setOnClickListener {
            Log.d("NOTIFY", "onCreateView: delete notification")
            val id = notifications[0].notification.notificationId
            notificationViewModel.deleteById(id)
        }

        return view
    }
}