package com.ala158.magicpantry.data

import androidx.room.Entity

@Entity(tableName = "ingredient_notification_cross_ref", primaryKeys = ["ingredientId", "notificationId"])
data class IngredientNotificationCrossRef(
    var ingredientId: Long,
    var notificationId: Long,
)
