package com.muss_coding.smartagriculturestartupapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_entity")
data class UserEntity(
    @PrimaryKey val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String?,
    val phoneNumber: String,
    val hasBenefitedFromStateCollaboration: Boolean //in case the agriculture has already donated to the state collaboration
)
