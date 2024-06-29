package com.muss_coding.smartagriculturestartupapp.core.data.local.mapper

import com.muss_coding.smartagriculturestartupapp.core.data.local.entity.ControlEntity
import com.muss_coding.smartagriculturestartupapp.core.domain.model.Control

fun ControlEntity.toControl() = Control(
    id = id,
    isItSprinkling = isItSprinkling,
    isItWatering = isItWatering
)

fun Control.toControlEntity() = ControlEntity(
    id = id,
    isItSprinkling = isItSprinkling,
    isItWatering = isItWatering
)