package com.muss_coding.smartagriculturestartupapp.core.domain.model

import java.time.LocalDateTime

data class Monitoring(
    val waterTemperature: Double,
    val waterPh: Double,
    val soilMoisture: Double,
    val waterCapacity: Double,
    val lastUpdated: LocalDateTime
)
