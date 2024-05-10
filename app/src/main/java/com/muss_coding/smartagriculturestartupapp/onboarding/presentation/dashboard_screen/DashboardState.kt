package com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen

import java.time.LocalDateTime

data class DashboardState(
    val userName: String,
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
    val waterTemperature: Double = 25.0,
    val waterPh: Double = 5.61,
    val waterLevel: Double = 83.2,
    val soilMoisture: Int = 4095,
    val isSprinklingChecked: Boolean = false,
    val isWateringChecked: Boolean = false
)