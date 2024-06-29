package com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.utils

import com.muss_coding.smartagriculturestartupapp.R

data class FeatureParameter(
    val title: String,
    val image: Int
)

val featureParameters = listOf(
    FeatureParameter(
        title = "Soil Type",
        image = R.drawable.soi_type
    ),
    FeatureParameter(
        title = "Irrigation",
        image = R.drawable.irrigation
    ),
    FeatureParameter(
        title = "Crop Idea",
        image = R.drawable.soil_analysis
    )
)