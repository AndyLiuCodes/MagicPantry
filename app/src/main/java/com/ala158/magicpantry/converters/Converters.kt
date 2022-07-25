package com.ala158.magicpantry.converters

import androidx.room.TypeConverter
import java.util.*

class Converters {

    @TypeConverter
    fun fromLongString(calendarString: Long?): Calendar? {
        return if (calendarString == null) {
            null
        } else {
            val cal = GregorianCalendar()
            cal.timeInMillis = calendarString
            return cal
        }
    }

    @TypeConverter
    fun fromCalendar(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }
}