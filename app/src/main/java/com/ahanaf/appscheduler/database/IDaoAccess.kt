package com.ahanaf.appscheduler.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ahanaf.appscheduler.models.ScheduleAppInfo

@Dao
interface IDaoAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(scheduleAppInfo: ScheduleAppInfo):Long

    @Query("SELECT * FROM scheduleappinfo ORDER BY id DESC")
    suspend fun getAllSchedule():List<ScheduleAppInfo>

    @Query("SELECT * FROM scheduleappinfo ORDER BY id DESC")
    fun getAllScheduleLiveData(): LiveData<List<ScheduleAppInfo>>

    @Update
    suspend fun updateSchedule(scheduleAppInfo: ScheduleAppInfo)

    @Delete
    suspend fun deleteSchedule(scheduleAppInfo: ScheduleAppInfo)
}