package com.muss_coding.smartagriculturestartupapp.onboarding.data.remote.dto

data class WeatherResponseDto(
    val data: WeatherDataDto? = null,
    val location: LocationDto? = null
)

data class LocationDto(
    val lat: Double? = null,
    val lon: Double? = null,
    val name: String? = null,
    val type: String? = null
)