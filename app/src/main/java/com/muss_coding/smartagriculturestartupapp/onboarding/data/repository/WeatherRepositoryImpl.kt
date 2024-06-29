package com.muss_coding.smartagriculturestartupapp.onboarding.data.repository

import com.muss_coding.smartagriculturestartupapp.onboarding.data.remote.WeatherRemoteDataSource
import com.muss_coding.smartagriculturestartupapp.onboarding.data.remote.dto.WeatherResponseDto
import com.muss_coding.smartagriculturestartupapp.onboarding.domain.model.WeatherData
import com.muss_coding.smartagriculturestartupapp.onboarding.domain.repository.WeatherRepository


class WeatherRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource
) : WeatherRepository {

    override suspend fun getWeather(location: String, apiKey: String): Result<WeatherData> {
        return try {
            val responseDto = remoteDataSource.getWeather(location, apiKey)
            val weatherData = responseDto.toDomainModel()
            Result.success(weatherData)
        } catch (e: Exception) {
            Result.failure(e) // Handle network or parsing errors
        }
    }

    // Extension function to convert DTO to Domain Model
    private fun WeatherResponseDto.toDomainModel(): WeatherData {
        return WeatherData(
            time = data?.time,
            locationName = location?.name,
            temperature = data?.values?.temperature,
            apparentTemperature = data?.values?.temperatureApparent,
            humidity = data?.values?.humidity,
            windSpeed = data?.values?.windSpeed,
            windDirection = data?.values?.windDirection,
            weatherCode = data?.values?.weatherCode,
            pressure = data?.values?.pressureSurfaceLevel
        )
    }
}
