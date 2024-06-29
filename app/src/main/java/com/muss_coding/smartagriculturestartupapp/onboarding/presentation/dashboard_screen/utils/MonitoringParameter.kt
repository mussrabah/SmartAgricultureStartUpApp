package com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.muss_coding.smartagriculturestartupapp.R

data class MonitoringParameter(
    val title: String,
    val value: String,
    val icon: Int
)

val monitoringParameters = listOf(
    MonitoringParameter(
        title = "Water temperature",
        value = "25°C",
        icon = R.drawable.temperature_svgrepo_com
    ),
    MonitoringParameter(
        title = "Humidity",
        value = "5.61",
        icon = R.drawable.humidity_svgrepo_com
    ),
    MonitoringParameter(
        title = "Soil moisture",
        value = "4095",
        icon = R.drawable.plant_svgrepo_com
    ),
    MonitoringParameter(
        title = "Water capacity",
        value = "83.2%",
        icon = R.drawable.weather_humidity_rain_svgrepo_com
    ),
    MonitoringParameter(
        title = "Pressure",
        value = "",
        icon = R.drawable.pressure_meter_svgrepo_com
    ),
    MonitoringParameter(
        title = "Wind",
        value = "",
        icon = R.drawable.wind_svg_svgrepo_com
    ),
    MonitoringParameter(
        title = "Wind Direction",
        value = "",
        icon = R.drawable.wind_direction_svgrepo_com
    )
)
