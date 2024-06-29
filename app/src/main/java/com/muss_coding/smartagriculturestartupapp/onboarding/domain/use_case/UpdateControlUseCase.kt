package com.muss_coding.smartagriculturestartupapp.onboarding.domain.use_case

import com.muss_coding.smartagriculturestartupapp.core.domain.model.Control
import com.muss_coding.smartagriculturestartupapp.core.domain.repository.Repository

class UpdateControlUseCase(
    private val repository: Repository
) {
    suspend operator fun invoke(isItSprinkling: Boolean, isItWatering: Boolean) {
        repository.updateControl(control = Control(
            id = 1,
            isItSprinkling = isItSprinkling,
            isItWatering = isItWatering
        ))
    }
}
