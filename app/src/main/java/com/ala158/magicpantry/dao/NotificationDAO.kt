package com.ala158.magicpantry.dao

import androidx.room.*
import com.ala158.magicpantry.data.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDAO {

    @Transaction
    @Query("SELECT * FROM notification")
    fun getAllNotifications(): Flow<List<NotificationWithIngredients>>

    @Insert
    suspend fun insertNotification(notification: Notification): Long

    // Adding to the ingredient list
    @Query("INSERT INTO ingredient_notification_cross_ref (ingredientId,notificationId ) VALUES (:ingredientId, :notificationId)")
    suspend fun insertNotificationCrossRef(notificationId: Long, ingredientId: Long)

    @Query("DELETE FROM notification where notificationId = :key")
    suspend fun deleteNotificationById(key: Long)

    // Deleting all ingredients from a recipe
    @Query("DELETE FROM ingredient_notification_cross_ref where notificationId = :key")
    suspend fun deleteAllNotificationCrossRefById(key: Long)
}