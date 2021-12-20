package com.ahanaf.appscheduler.ui.schedule

import androidx.lifecycle.LiveData
import com.ahanaf.appscheduler.database.IDaoAccess
import com.ahanaf.appscheduler.models.ScheduleAppInfo

class ScheduleRepository (private val iDaoAccess: IDaoAccess){
    suspend fun insertSchedule(scheduleAppInfo: ScheduleAppInfo):Long = iDaoAccess.insertSchedule(scheduleAppInfo)

    suspend fun updateSchedule(scheduleAppInfo: ScheduleAppInfo) = iDaoAccess.updateSchedule(scheduleAppInfo)

    suspend fun deleteSchedule(scheduleAppInfo: ScheduleAppInfo) = iDaoAccess.deleteSchedule(scheduleAppInfo)

    val allSchedule : LiveData<List<ScheduleAppInfo>> = iDaoAccess.getAllScheduleLiveData()

    suspend fun getAllSchedule() : List<ScheduleAppInfo> = iDaoAccess.getAllSchedule()

}