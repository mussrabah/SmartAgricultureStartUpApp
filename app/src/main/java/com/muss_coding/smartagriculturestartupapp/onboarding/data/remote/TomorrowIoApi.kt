package com.muss_coding.smartagriculturestartupapp.onboarding.data.remote

import com.muss_coding.smartagriculturestartupapp.onboarding.data.remote.dto.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query


interface TomorrowIoApi {
    @GET("v4/weather/realtime")
    suspend fun getWeather(
        @Query("location") location: String,
        @Query("apikey") apiKey: String,
        @Query("fields") fields: String = "temperature,temperatureApparent,humidity,windSpeed,windDirection,weatherCode"
    ): WeatherResponseDto

    companion object {
        const val BASE_URL = "https://api.tomorrow.io/"
    }
}