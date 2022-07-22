package com.ala158.magicpantry.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "notification")
data class Notification(
    @PrimaryKey(autoGenerate = true)
    var notificationId: Long = 0L,

    @ColumnInfo(name = "date")
    var date: Calendar,

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "is_read")
    var isRead: Boolean = false,
)
