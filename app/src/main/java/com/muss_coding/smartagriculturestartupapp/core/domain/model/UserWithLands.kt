package com.muss_coding.smartagriculturestartupapp.core.domain.model

data class UserWithLands(
    val user: User,
    val lands: List<Land>
)
