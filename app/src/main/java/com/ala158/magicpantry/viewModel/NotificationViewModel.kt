package com.ala158.magicpantry.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.Notification
import com.ala158.magicpantry.data.NotificationWithIngredients
import com.ala158.magicpantry.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationViewModel(private val repository: NotificationRepository) : ViewModel() {

    val allNotifications: LiveData<List<NotificationWithIngredients>> =
        repository.allNotifications.asLiveData()

    fun insert(
        notification: Notification,
        ingredients: List<Ingredient>,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = repository.insertNotification(notification)
            for (ingredient in ingredients) {
                insertCrossRef(id, ingredient.ingredientId)
            }
        }
    }

    private fun insertCrossRef(notificationId: Long, ingredientId: Long) {
        repository.insertNotificationCrossRef(notificationId, ingredientId)
    }

    fun deleteById(key: Long) {
        repository.deleteNotificationById(key)
        repository.deleteAllNotificationCrossRefById(key)
    }

    fun updateRead(notification: Notification) {
        repository.updateRead(notification)
    }
}