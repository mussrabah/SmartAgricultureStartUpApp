package com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen

import android.app.Application
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muss_coding.smartagriculturestartupapp.core.domain.model.Control
import com.muss_coding.smartagriculturestartupapp.core.domain.model.Monitoring
import com.muss_coding.smartagriculturestartupapp.core.domain.repository.Repository
import com.muss_coding.smartagriculturestartupapp.onboarding.domain.repository.WeatherRepository
import com.muss_coding.smartagriculturestartupapp.onboarding.domain.use_case.GetMonitoringData
import com.muss_coding.smartagriculturestartupapp.onboarding.domain.use_case.UpdateControlUseCase
import com.muss_coding.smartagriculturestartupapp.onboarding.domain.util.DateFormatting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getMonitoringData: GetMonitoringData,
    private val updateControlUseCase: UpdateControlUseCase,
    private val repository: Repository,
    private val weatherRepository: WeatherRepository
): ViewModel() {

    private val apiKey = "8iNqhzHhTSwc0tjAR6WupLMLZN62gfvl"

    private val _state = MutableStateFlow(DashboardState(userName = "Imad"))
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val _location = MutableStateFlow<Location?>(null)

    init {
        initializeState()
        observeLocationUpdates()
    }

    private fun initializeState() {
        viewModelScope.launch {
            val control: Control = repository.getControlById(1) ?: Control(
                isItSprinkling = false,
                isItWatering = false
            )
            _state.value = _state.value.copy(
                isSprinklingChecked = control.isItSprinkling,
                isWateringChecked = control.isItWatering
            )
        }
        getMonitoringData.getMonitoringData()
            .onEach {
                if (it.isEmpty()) return@onEach
                _state.value = _state.value.copy(
                    lastUpdated = DateFormatting.formatDatabaseDate(it.last().lastUpdated),
                    waterTemperature = it.last().waterTemperature,
                    weatherData = _state.value.weatherData
                    ?.copy(
                        temperature = it.last().waterTemperature,
                        time = DateFormatting.formatDatabaseDate(it.last().lastUpdated)
                    )
                )
            }.launchIn(viewModelScope)
    }

    private fun observeLocationUpdates() {
        viewModelScope.launch {
            _location.collect { location ->
                location?.let {
                    Log.d("DVM", "observeLocationUpdates: ${it.latitude}, ${it.longitude}")
                    fetchWeather("${it.latitude},${it.longitude}")
                }
            }
        }
    }

    fun updateLocation(location: Location) {
        _location.value = location
    }

    private fun fetchWeather(location: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            weatherRepository.getWeather(location, apiKey)
                .onSuccess { data ->
                    repository.insertMonitoringData(Monitoring(
                            waterTemperature = data.temperature ?: 0.0,
                            waterPh = 8.5,
                            soilMoisture = 4095.0,
                            waterCapacity = 83.2,
                            lastUpdated = DateFormatting.formatAPiDate(data.time)
                            ))
                    _state.value = _state.value.copy(
                        weatherData = data,
                        error = null,
                        isLoading = false,
                        lastUpdated = DateFormatting.formatDatabaseDate(DateFormatting.formatAPiDate(data.time))
                    )
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
        }
    }

    fun onEvent(event: DashboardEvents) {
        when (event) {
            is DashboardEvents.OnUpdateControlToggle -> {
                when (event.index) {
                    0 -> viewModelScope.launch {
                        _state.value = _state.value.copy(
                            isSprinklingChecked = event.isChecked
                        )
                    }
                    1 -> viewModelScope.launch {
                        _state.value = _state.value.copy(
                            isWateringChecked = event.isChecked
                        )
                    }
                    else -> Unit
                }
                viewModelScope.launch {
                    updateControlUseCase(_state.value.isSprinklingChecked, _state.value.isWateringChecked)
                }
            }
        }
    }
}

