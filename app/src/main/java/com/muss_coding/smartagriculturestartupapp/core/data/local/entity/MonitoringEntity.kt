package com.muss_coding.smartagriculturestartupapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.muss_coding.smartagriculturestartupapp.core.data.local.converters.OffsetDateTimeConverter
import java.time.OffsetDateTime

@Entity(tableName = "monitoring_entity")
@TypeConverters(OffsetDateTimeConverter::class)
data class MonitoringEntity(
    @PrimaryKey val id: Int? = null,
    val waterTemperature: Double,
    val waterPh: Double,
    val soilMoisture: Double,
    val waterCapacity: Double,
    val lastUpdated: OffsetDateTime
)