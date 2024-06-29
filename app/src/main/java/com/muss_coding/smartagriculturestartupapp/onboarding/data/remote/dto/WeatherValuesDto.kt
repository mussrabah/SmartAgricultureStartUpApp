package com.muss_coding.smartagriculturestartupapp.onboarding.data.remote.dto

data class WeatherValuesDto(
    val cloudBase: Double? = null,
    val cloudCeiling: Double? = null,
    val cloudCover: Int? = null,
    val dewPoint: Double? = null,
    val freezingRainIntensity: Int? = null,
    val humidity: Int? = null,
    val precipitationProbability: Int? = null,
    val pressureSurfaceLevel: Double? = null,
    val rainIntensity: Int? = null,
    val sleetIntensity: Int? = null,
    val snowIntensity: Int? = null,
    val temperature: Double? = null,
    val temperatureApparent: Double? = null,
    val uvHealthConcern: Int? = null,
    val uvIndex: Int? = null,
    val visibility: Double? = null,
    val weatherCode: Int? = null,
    val windDirection: Double? = null,
    val windGust: Double? = null,
    val windSpeed: Double? = null
)