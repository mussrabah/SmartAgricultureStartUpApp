package com.muss_coding.smartagriculturestartupapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "control_entity")
data class ControlEntity(
    @PrimaryKey val id: Int? = null,
    val isItSprinkling: Boolean,
    val isItWatering: Boolean
)