package com.ala158.magicpantry.data

import androidx.room.Entity

@Entity(primaryKeys = ["ingredientId", "notificationId"])
data class IngredientNotificationCrossRef(
    var ingredientId: Long,
    var notificationId: Long,
)
