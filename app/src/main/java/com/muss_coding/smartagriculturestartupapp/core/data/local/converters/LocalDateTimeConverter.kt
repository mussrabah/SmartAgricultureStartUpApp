package com.muss_coding.smartagriculturestartupapp.core.data.local.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

@TypeConverter
fun LocalDateTime.toOffsetDateTime(): OffsetDateTime {
    return this.atOffset(ZoneOffset.UTC)
}

@TypeConverter
fun OffsetDateTime.fromOffsetDateTime(): LocalDateTime {
    return this.toLocalDateTime()
}