package com.muss_coding.smartagriculturestartupapp.core.data.local.mapper

import com.muss_coding.smartagriculturestartupapp.core.data.local.entity.UserWithLandsEntity
import com.muss_coding.smartagriculturestartupapp.core.domain.model.UserWithLands

fun UserWithLandsEntity.toUserWithLands(): UserWithLands = UserWithLands(
    user = user.toUser(),
    lands = lands.map { it.toLand() }
)

fun UserWithLands.toUserWithLandsEntity(): UserWithLandsEntity = UserWithLandsEntity(
    user = user.toUserEntity(),
    lands = lands.map { it.toLandEntity() }
)