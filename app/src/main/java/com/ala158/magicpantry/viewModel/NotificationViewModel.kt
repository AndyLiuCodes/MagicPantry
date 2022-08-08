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

    private val _newNotificationId = MutableLiveData(0L)
    val newNotificationId: LiveData<Long> = _newNotificationId

    val allNotifications: LiveData<List<NotificationWithIngredients>> =
        repository.allNotifications.asLiveData()

    private var _currNotification: LiveData<NotificationWithIngredients> = MutableLiveData()
    val currNotification: LiveData<NotificationWithIngredients> = _currNotification

    fun getById(key: Long): LiveData<NotificationWithIngredients> {
        return repository.getNotificationById(key).asLiveData()
    }


    fun getByIdSync(key: Long): NotificationWithIngredients {
        return repository.getNotificationByIdSync(key)
    }

    fun insert(
        notification: Notification,
        ingredients: List<Ingredient>,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = repository.insertNotification(notification)
            _newNotificationId.postValue(id)
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