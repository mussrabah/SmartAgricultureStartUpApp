package com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muss_coding.smartagriculturestartupapp.onboarding.domain.use_case.GetDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDateUseCase: GetDateUseCase
): ViewModel() {
    var state by mutableStateOf(DashboardState(userName = "Imad", lastUpdated = getDateUseCase()))

    /*private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()*/

    fun onEvent(event: DashboardEvents) {
        when (event) {
            is DashboardEvents.OnUpdateControlToggle -> {
                when(event.index) {
                    0 -> viewModelScope.launch {
                        state = state.copy(
                            isSprinklingChecked = event.isChecked
                        )
                    }
                    1 -> viewModelScope.launch {
                        state = state.copy(
                            isWateringChecked = event.isChecked
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}