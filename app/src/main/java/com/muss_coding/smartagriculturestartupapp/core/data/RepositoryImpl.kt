package com.muss_coding.smartagriculturestartupapp.core.data

import com.muss_coding.smartagriculturestartupapp.core.data.local.Dao
import com.muss_coding.smartagriculturestartupapp.core.data.local.converters.toOffsetDateTime
import com.muss_coding.smartagriculturestartupapp.core.data.local.mapper.toControl
import com.muss_coding.smartagriculturestartupapp.core.data.local.mapper.toControlEntity
import com.muss_coding.smartagriculturestartupapp.core.data.local.mapper.toLand
import com.muss_coding.smartagriculturestartupapp.core.data.local.mapper.toLandEntity
import com.muss_coding.smartagriculturestartupapp.core.data.local.mapper.toMonitoring
import com.muss_coding.smartagriculturestartupapp.core.data.local.mapper.toMonitoringEntity
import com.muss_coding.smartagriculturestartupapp.core.data.local.mapper.toUser
import com.muss_coding.smartagriculturestartupapp.core.data.local.mapper.toUserEntity
import com.muss_coding.smartagriculturestartupapp.core.data.local.mapper.toUserWithLands
import com.muss_coding.smartagriculturestartupapp.core.domain.model.Control
import com.muss_coding.smartagriculturestartupapp.core.domain.model.Land
import com.muss_coding.smartagriculturestartupapp.core.domain.model.Monitoring
import com.muss_coding.smartagriculturestartupapp.core.domain.model.User
import com.muss_coding.smartagriculturestartupapp.core.domain.model.UserWithLands
import com.muss_coding.smartagriculturestartupapp.core.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class RepositoryImpl(
    private val dao: Dao
): Repository {
    override suspend fun insertControl(control: Control) {
        dao.insertControl(control.toControlEntity())
    }

    override suspend fun updateControl(control: Control) {
        dao.updateControl(control.toControlEntity())
    }

    override suspend fun deleteControl(control: Control) {
        dao.deleteControl(control.toControlEntity())
    }

    override suspend fun getControlById(id: Int): Control? {
        return dao.getControlById(id)?.toControl()
    }

    override fun getAllControls(): Flow<List<Control>> {
        return dao.getAllControls()
            .map { controls ->
                controls.map {
                    it.toControl()
                }
        }
    }

    override suspend fun insertLand(land: Land) {
        dao.insertLand(land.toLandEntity())
    }

    override suspend fun updateLand(land: Land) {
        dao.updateLand(land.toLandEntity())
    }

    override suspend fun deleteLand(land: Land) {
        dao.deleteLand(land.toLandEntity())
    }

    override suspend fun getLandById(id: Int): Land? {
        return dao.getLandById(id)?.toLand()
    }

    override fun getAllLands(): Flow<List<Land>> {
        return dao.getAllLands()
            .map {lands ->
                lands.map {
                    it.toLand()
                }
            }
    }

    override fun getLandsByOwnerId(ownerId: Int): Flow<List<Land>> {
       return dao.getLandsByOwnerId(ownerId)
           .map {lands ->
               lands.map {
                   it.toLand()
               }
           }
    }

    override suspend fun insertMonitoringData(monitoringEntity: Monitoring) {
        dao.insertMonitoringData(monitoringEntity.toMonitoringEntity())
    }

    override suspend fun updateMonitoringData(monitoringEntity: Monitoring) {
        dao.updateMonitoringData(monitoringEntity.toMonitoringEntity())
    }

    override suspend fun deleteMonitoringData(monitoringEntity: Monitoring) {
        dao.deleteMonitoringData(monitoringEntity.toMonitoringEntity())
    }

    override suspend fun getMonitoringDataById(id: Int): Monitoring? {
        return dao.getMonitoringDataById(id)?.toMonitoring()
    }

    override fun getAllMonitoringData(): Flow<List<Monitoring>> {
        return dao.getAllMonitoringData()
            .map { monitoringList ->
                monitoringList.map {
                    it.toMonitoring()
                }
            }
    }

    override suspend fun insertUser(user: User) {
        dao.insertUser(user.toUserEntity())
    }

    override suspend fun updateUser(user: User) {
        dao.updateUser(user.toUserEntity())
    }

    override suspend fun deleteUser(user: User) {
        dao.deleteUser(user.toUserEntity())
    }

    override suspend fun getUserById(id: Int): User? {
        return dao.getUserById(id)?.toUser()
    }

    override suspend fun getUserByEmail(email: String): User? {
        return dao.getUserByEmail(email)?.toUser()
    }

    override fun getAllUsers(): Flow<List<User>> {
        return dao.getAllUsers()
            .map { users ->
                users.map {
                    it.toUser()
                }
            }
    }

    override suspend fun getUserWithLands(userId: Int): UserWithLands? {
        return dao.getUserWithLands(userId)?.toUserWithLands()
    }
}