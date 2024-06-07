package com.muss_coding.smartagriculturestartupapp.onboarding.domain.use_case

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GetDateUseCase {
    operator fun invoke(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val formattedDateTime = currentDateTime.format(formatter)
        return formattedDateTime
    }
}