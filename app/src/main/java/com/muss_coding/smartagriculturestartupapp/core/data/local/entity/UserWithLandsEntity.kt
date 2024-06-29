package com.muss_coding.smartagriculturestartupapp.core.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation


data class UserWithLandsEntity(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "ownerId"
    )
    val lands: List<LandEntity>
)