package com.muss_coding.smartagriculturestartupapp.onboarding.domain.model

data class WeatherData(
    val time: String? = null,
    val locationName: String? = null,
    val temperature: Double? = null,
    val apparentTemperature: Double? = null,
    val humidity: Int? = null,
    val windSpeed: Double? = null,
    val windDirection: Double? = null,
    val weatherCode: Int? = null,
    val pressure: Double? = null
)