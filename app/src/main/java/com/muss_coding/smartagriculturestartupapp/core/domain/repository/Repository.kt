package com.muss_coding.smartagriculturestartupapp.core.domain.repository

import com.muss_coding.smartagriculturestartupapp.core.domain.model.Control
import com.muss_coding.smartagriculturestartupapp.core.domain.model.Land
import com.muss_coding.smartagriculturestartupapp.core.domain.model.Monitoring
import com.muss_coding.smartagriculturestartupapp.core.domain.model.User
import com.muss_coding.smartagriculturestartupapp.core.domain.model.UserWithLands
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface Repository {
    // Control Functions
    suspend fun insertControl(control: Control)
    suspend fun updateControl(control: Control)
    suspend fun deleteControl(control: Control)
    suspend fun getControlById(id: Int): Control?
    fun getAllControls(): Flow<List<Control>>

    // Land Functions
    suspend fun insertLand(land: Land)
    suspend fun updateLand(land: Land)
    suspend fun deleteLand(land: Land)
    suspend fun getLandById(id: Int): Land?
    fun getAllLands(): Flow<List<Land>>
    fun getLandsByOwnerId(ownerId: Int): Flow<List<Land>>

    // Monitoring Functions
    suspend fun insertMonitoringData(monitoringEntity: Monitoring)
    suspend fun updateMonitoringData(monitoringEntity: Monitoring)
    suspend fun deleteMonitoringData(monitoringEntity: Monitoring)
    suspend fun getMonitoringDataById(id: Int): Monitoring?
    fun getAllMonitoringData(): Flow<List<Monitoring>>

    // User Functions
    suspend fun insertUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
    suspend fun getUserById(id: Int): User?
    suspend fun getUserByEmail(email: String): User?
    fun getAllUsers(): Flow<List<User>>

    // UserWithLands Function
    suspend fun getUserWithLands(userId: Int): UserWithLands?
}
