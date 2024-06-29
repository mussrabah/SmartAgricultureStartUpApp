package com.muss_coding.smartagriculturestartupapp.onboarding.data.remote.dto

data class WeatherDataDto(
    val time: String? = null, // Assuming ISO 8601 format
    val values: WeatherValuesDto? = null
)