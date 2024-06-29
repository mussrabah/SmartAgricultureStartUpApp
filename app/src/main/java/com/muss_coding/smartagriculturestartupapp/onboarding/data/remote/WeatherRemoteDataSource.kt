package com.muss_coding.smartagriculturestartupapp.onboarding.data.remote

import com.muss_coding.smartagriculturestartupapp.onboarding.data.remote.dto.WeatherResponseDto

class WeatherRemoteDataSource(private val api: TomorrowIoApi) {
    suspend fun getWeather(location: String, apiKey: String): WeatherResponseDto {
        return api.getWeather(location, apiKey)
    }
}
