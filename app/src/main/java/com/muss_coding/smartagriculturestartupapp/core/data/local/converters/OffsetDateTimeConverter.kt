package com.muss_coding.smartagriculturestartupapp.core.data.local.converters

import androidx.room.TypeConverter
import java.time.OffsetDateTime

class OffsetDateTimeConverter {
    @TypeConverter
    fun fromOffsetDateTime(value: OffsetDateTime?): String? {
        return value?.toString()  // Convert OffsetDateTime to String (ISO 8601 format)
    }

    @TypeConverter
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let { OffsetDateTime.parse(it) } // Parse String back to OffsetDateTime
    }
}
