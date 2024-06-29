package com.muss_coding.smartagriculturestartupapp.core.data.local.mapper

import com.muss_coding.smartagriculturestartupapp.core.data.local.entity.MonitoringEntity
import com.muss_coding.smartagriculturestartupapp.core.domain.model.Monitoring
import java.time.ZoneOffset

fun MonitoringEntity.toMonitoring(): Monitoring {
    return Monitoring(
        waterTemperature = waterTemperature,
        waterPh = waterPh,
        waterCapacity = waterCapacity,
        soilMoisture = soilMoisture,
        lastUpdated = lastUpdated.toLocalDateTime()
    )
}

fun Monitoring.toMonitoringEntity(): MonitoringEntity {
    return MonitoringEntity(
        waterTemperature = waterTemperature,
        waterPh = waterPh,
        waterCapacity = waterCapacity,
        soilMoisture = soilMoisture,
        lastUpdated = lastUpdated.atOffset(ZoneOffset.UTC)
    )
}