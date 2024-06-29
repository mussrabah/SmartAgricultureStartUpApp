package com.muss_coding.smartagriculturestartupapp.onboarding.domain.util

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateFormatting {
    fun formatAPiDate(date: String?): LocalDateTime {
        return OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime()
    }

    fun formatDatabaseDate(date: LocalDateTime?): String {
        val currentDateTime = date?.atZone(ZoneId.of("GMT+1"))
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val formattedDateTime = currentDateTime?.format(formatter)
        return formattedDateTime ?: "no date time"
    }
}