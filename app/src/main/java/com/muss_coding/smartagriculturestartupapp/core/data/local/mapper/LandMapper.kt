package com.muss_coding.smartagriculturestartupapp.core.data.local.mapper

import com.muss_coding.smartagriculturestartupapp.core.data.local.entity.LandEntity
import com.muss_coding.smartagriculturestartupapp.core.domain.model.Land

fun LandEntity.toLand(): Land = Land(
    id,
    ownerId
)

fun Land.toLandEntity(): LandEntity = LandEntity(
    id,
    ownerId
)