package com.muss_coding.smartagriculturestartupapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "land_entity")
data class LandEntity(
    @PrimaryKey val id: Int? = null,
    val ownerId: Int?,
)