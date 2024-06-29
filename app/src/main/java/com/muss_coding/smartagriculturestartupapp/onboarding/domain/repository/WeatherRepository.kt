package com.muss_coding.smartagriculturestartupapp.onboarding.domain.repository

import com.muss_coding.smartagriculturestartupapp.onboarding.domain.model.WeatherData

interface WeatherRepository {
    suspend fun getWeather(location: String, apiKey: String): Result<WeatherData>
}
