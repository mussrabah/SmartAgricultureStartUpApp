package com.muss_coding.smartagriculturestartupapp.core.domain.model

data class User(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String?,
    val phoneNum: String,
    val hasBenefitedFromStateCollaboration: Boolean
)
