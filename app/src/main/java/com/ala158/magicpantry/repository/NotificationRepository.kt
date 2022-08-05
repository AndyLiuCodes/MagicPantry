package com.ala158.magicpantry.repository

import com.ala158.magicpantry.dao.NotificationDAO
import com.ala158.magicpantry.data.Notification
import com.ala158.magicpantry.data.NotificationWithIngredients
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NotificationRepository(private val notificationDAO: NotificationDAO) {

    val allNotifications: Flow<List<NotificationWithIngredients>> =
        notificationDAO.getAllNotifications()

    suspend fun getNotificationById(key: Long): Flow<NotificationWithIngredients> {
        return notificationDAO.getNotificationById(key)
    }

    fun getNotificationByIdSync(key: Long): NotificationWithIngredients {
        return notificationDAO.getNotificationByIdSync(key)
    }

    suspend fun insertNotification(notification: Notification): Long {
        return notificationDAO.insertNotification(notification)
    }

    fun insertNotificationCrossRef(notificationId: Long, ingredientId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            notificationDAO.insertNotificationCrossRef(notificationId, ingredientId)
        }
    }

    fun deleteNotificationById(key: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            notificationDAO.deleteNotificationById(key)
        }
    }

    fun deleteAllNotificationCrossRefById(key: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            notificationDAO.deleteAllNotificationCrossRefById(key)
        }
    }

    fun updateRead(notification: Notification) {
        CoroutineScope(Dispatchers.IO).launch {
            notificationDAO.updateRead(notification)
        }
    }
}
