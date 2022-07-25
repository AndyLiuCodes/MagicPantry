package com.ala158.magicpantry.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.util.*

@Entity(tableName = "notification")
data class Notification(
    @PrimaryKey(autoGenerate = true)
    var notificationId: Long = 0L,

    @ColumnInfo(name = "date")
    var date: Calendar = Calendar.getInstance(),

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "is_read")
    var isRead: Boolean = false,
)
