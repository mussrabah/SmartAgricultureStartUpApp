package com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.utils

import com.muss_coding.smartagriculturestartupapp.R

data class ControlParameter(
    val title: String,
    val isItSelected: Boolean,
    val icon: Int
)

val controlParameters = mutableListOf(
    ControlParameter(
        title = "Sprinkling",
        isItSelected = true,
        icon = R.drawable.sprinkling
    ),
    ControlParameter(
        title = "Water pump",
        isItSelected = false,
        icon = R.drawable.water_pump
    ),
)
