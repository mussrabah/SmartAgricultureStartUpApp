package com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen

import com.muss_coding.smartagriculturestartupapp.onboarding.domain.model.WeatherData

data class DashboardState(
    val userName: String,
    val lastUpdated: String = "",
    val waterTemperature: Double = 25.0,
    val waterPh: Double = 5.61,
    val waterLevel: Double = 83.2,
    val soilMoisture: Int = 4095,
    val isSprinklingChecked: Boolean = false,
    val isWateringChecked: Boolean = false,
    val weatherData: WeatherData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
