package com.ala158.magicpantry.dao

import androidx.room.*
import com.ala158.magicpantry.data.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDAO {

    @Transaction
    @Query("SELECT * FROM notification")
    fun getAllNotifications(): Flow<List<NotificationWithIngredients>>

    @Transaction
    @Query("SELECT * FROM notification WHERE notificationId = :key")
    fun getNotificationById(key: Long): Flow<NotificationWithIngredients>

    @Transaction
    @Query("SELECT * FROM notification WHERE notificationId = :key")
    fun getNotificationByIdSync(key: Long): NotificationWithIngredients

    @Insert
    suspend fun insertNotification(notification: Notification): Long

    // Adding to the notification's ingredient list
    @Query("INSERT INTO ingredient_notification_cross_ref (ingredientId,notificationId ) VALUES (:ingredientId, :notificationId)")
    suspend fun insertNotificationCrossRef(notificationId: Long, ingredientId: Long)

    @Query("DELETE FROM notification where notificationId = :key")
    suspend fun deleteNotificationById(key: Long)

    // Deleting all ingredients from a notification
    @Query("DELETE FROM ingredient_notification_cross_ref where notificationId = :key")
    suspend fun deleteAllNotificationCrossRefById(key: Long)

    //update value of is_read
    @Update
    suspend fun updateRead(notification: Notification)
}