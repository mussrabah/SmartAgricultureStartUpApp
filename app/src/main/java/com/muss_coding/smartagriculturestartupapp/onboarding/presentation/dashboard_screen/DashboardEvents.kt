package com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen

import com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.utils.FeatureParameter

sealed class DashboardEvents {
    data class OnUpdateControlToggle(val index: Int, val isChecked: Boolean) : DashboardEvents()
}