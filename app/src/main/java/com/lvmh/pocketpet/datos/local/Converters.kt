package com.lvmh.pocketpet.datos.local

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    // 1. De Long (número) a Date (fecha)
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    // 2. De Date (fecha) a Long (número)
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
