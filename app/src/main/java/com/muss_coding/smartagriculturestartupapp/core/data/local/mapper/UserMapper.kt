package com.muss_coding.smartagriculturestartupapp.core.data.local.mapper

import com.muss_coding.smartagriculturestartupapp.core.data.local.entity.UserEntity
import com.muss_coding.smartagriculturestartupapp.core.domain.model.User

fun UserEntity.toUser(): User = User(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    password = password,
    phoneNum = phoneNumber,
    hasBenefitedFromStateCollaboration = hasBenefitedFromStateCollaboration
)

fun User.toUserEntity(): UserEntity = UserEntity(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    password = password,
    phoneNumber = phoneNum,
    hasBenefitedFromStateCollaboration = hasBenefitedFromStateCollaboration
)