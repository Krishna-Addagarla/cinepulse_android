package com.partner.cinepulse.data.local

import androidx.room.TypeConverter // Ensure you import the singular version
import java.util.Date

class Converters {
//    @TypeConverter // Use this for each conversion method
//    fun fromTimestamp(value: Long?): Date? {
//        return value?.let { com.google.type.Date(it) }
//    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}