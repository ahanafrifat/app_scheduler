package com.ahanaf.appscheduler.ui.schedule

import androidx.lifecycle.*
import com.ahanaf.appscheduler.application.MyApplication
import com.ahanaf.appscheduler.database.AppDatabase
import com.ahanaf.appscheduler.models.ScheduleAppInfo
import com.ahanaf.appscheduler.utils.AppUtils
import kotlinx.coroutines.launch

class ScheduleViewModel : ViewModel() {

    val message = MutableLiveData<String>()
    val insertScheduleId = MutableLiveData<Long>()
    private val repository: ScheduleRepository
    val scheduleAppInfo: LiveData<List<ScheduleAppInfo>>
    val isUpdateSuccessful = MutableLiveData<Boolean>()

    init {
        val dao = AppDatabase(MyApplication.myApplicationInstance!!).daoAccess()
        repository = ScheduleRepository(dao)
        scheduleAppInfo = repository.allSchedule
    }

    fun saveSchedule(appName: String, packageName: String, time: Long) {
        viewModelScope.launch {
            val scheduleAppInfo = ScheduleAppInfo(appName, packageName, time, true, null)
            val allSchedule = repository.getAllSchedule()
            if (isTimeExist(time, allSchedule)) {
                message.postValue("Schedule exist for same time")
            } else {
                insertScheduleId.postValue(repository.insertSchedule(scheduleAppInfo))
                message.postValue("Create Successful")
            }
        }
    }

    fun updateSchedule(
        appName: String,
        packageName: String,
        time: Long,
        scheduleApp: ScheduleAppInfo
    ) {
        viewModelScope.launch {
            val allSchedule = repository.getAllSchedule()
            if (isTimeExist(time, allSchedule)) {
                message.postValue("Schedule exist for same time")
                isUpdateSuccessful.postValue(false)
            } else {
                val scheduleAppInfo = ScheduleAppInfo(appName, packageName, time, true, null)
                scheduleAppInfo.id = scheduleApp.id
                repository.updateSchedule(scheduleAppInfo)
                message.postValue("Update Successful")
                isUpdateSuccessful.postValue(true)
            }
        }
    }

    fun deleteSchedule(scheduleApp: ScheduleAppInfo) {
        viewModelScope.launch {
            repository.deleteSchedule(scheduleApp)
            message.postValue("Delete Successful")
        }
    }

    private fun isTimeExist(time: Long, allSchedule: List<ScheduleAppInfo>): Boolean {
        val today = AppUtils.millisLongToDateMonthYearTimeString(time)
        var isTimeExist = false
        allSchedule.forEach { schedule ->
            schedule.time?.let { time ->
                val date = AppUtils.millisLongToDateMonthYearTimeString(time)
                if (date == today) {
                    isTimeExist = true
                }
            }
        }
        return isTimeExist
    }

}