package com.muss_coding.smartagriculturestartupapp.onboarding.domain.use_case

import com.muss_coding.smartagriculturestartupapp.core.domain.model.Monitoring
import com.muss_coding.smartagriculturestartupapp.core.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GetMonitoringData(
    private val repository: Repository
) {
//    suspend operator fun invoke(): String {
//        val currentDateTime = repository.getMonitoringDataById(1)?.lastUpdated?.atZone(ZoneId.of("GMT+1"))
//        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
//        val formattedDateTime = currentDateTime?.format(formatter)
//        return formattedDateTime ?: "no date time"
//    }

    fun getMonitoringData(): Flow<List<Monitoring>>{
        return repository.getAllMonitoringData()
    }
}