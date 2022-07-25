package com.ala158.magicpantry.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

// Many-to-Many Relationship REF: https://developer.android.com/training/data-storage/room/relationships
data class NotificationWithIngredients(
    @Embedded val notification: Notification,
    @Relation(
        parentColumn = "notificationId",
        entityColumn = "ingredientId",
        associateBy = Junction(IngredientNotificationCrossRef::class)
    )
    val ingredients: List<Ingredient>
)
