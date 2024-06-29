package com.muss_coding.smartagriculturestartupapp.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.muss_coding.smartagriculturestartupapp.core.data.local.entity.ControlEntity
import com.muss_coding.smartagriculturestartupapp.core.data.local.entity.LandEntity
import com.muss_coding.smartagriculturestartupapp.core.data.local.entity.MonitoringEntity
import com.muss_coding.smartagriculturestartupapp.core.data.local.entity.UserEntity
import com.muss_coding.smartagriculturestartupapp.core.data.local.entity.UserWithLandsEntity
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface Dao {

    // DAO functions for ControlEntity

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertControl(controlEntity: ControlEntity)

    // Update
    @Update
    suspend fun updateControl(controlEntity: ControlEntity)

    // Delete
    @Delete
    suspend fun deleteControl(controlEntity: ControlEntity)

    // Get (single entity by ID)
    @Query("SELECT * FROM control_entity WHERE id = :id")
    suspend fun getControlById(id: Int): ControlEntity?

    // Get all entities
    @Query("SELECT * FROM control_entity")
    fun getAllControls(): Flow<List<ControlEntity>>

    //DAO functions for LandEntity
    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLand(landEntity: LandEntity)

    // Update
    @Update
    suspend fun updateLand(landEntity: LandEntity)

    // Delete
    @Delete
    suspend fun deleteLand(landEntity: LandEntity)

    // Get (single entity by ID)
    @Query("SELECT * FROM land_entity WHERE id = :id")
    suspend fun getLandById(id: Int): LandEntity?

    // Get all entities
    @Query("SELECT * FROM land_entity")
    fun getAllLands(): Flow<List<LandEntity>>

    // Additional Queries (optional)
    @Query("SELECT * FROM land_entity WHERE ownerId = :ownerId")
    fun getLandsByOwnerId(ownerId: Int): Flow<List<LandEntity>>

    //DAO functions for MonitoringEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonitoringData(monitoringEntity: MonitoringEntity)

    @Update
    suspend fun updateMonitoringData(monitoringEntity: MonitoringEntity)

    @Delete
    suspend fun deleteMonitoringData(monitoringEntity: MonitoringEntity)

    @Query("SELECT * FROM monitoring_entity WHERE id = :id")
    suspend fun getMonitoringDataById(id: Int): MonitoringEntity?

    @Query("SELECT * FROM monitoring_entity")
    fun getAllMonitoringData(): Flow<List<MonitoringEntity>>


    //DAO functions for UserEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Replace if user with same ID exists
    suspend fun insertUser(userEntity: UserEntity)

    @Update
    suspend fun updateUser(userEntity: UserEntity)

    @Delete
    suspend fun deleteUser(userEntity: UserEntity)

    @Query("SELECT * FROM user_entity WHERE id = :id")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT * FROM user_entity WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM user_entity")
    fun getAllUsers(): Flow<List<UserEntity>>

    //DAO functions for UserWithLands
    @Transaction  // Important for handling relations
    @Query("SELECT * FROM user_entity WHERE id = :userId")
    fun getUserWithLands(userId: Int): UserWithLandsEntity?
}