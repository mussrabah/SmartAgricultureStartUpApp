package com.muss_coding.smartagriculturestartupapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.muss_coding.smartagriculturestartupapp.core.data.local.entity.ControlEntity
import com.muss_coding.smartagriculturestartupapp.core.data.local.entity.LandEntity
import com.muss_coding.smartagriculturestartupapp.core.data.local.entity.MonitoringEntity
import com.muss_coding.smartagriculturestartupapp.core.data.local.entity.UserEntity
import com.muss_coding.smartagriculturestartupapp.core.data.local.entity.UserWithLandsEntity

@Database(
    entities = [ControlEntity::class, LandEntity::class, MonitoringEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class Database: RoomDatabase() {
    abstract val dao: Dao
}