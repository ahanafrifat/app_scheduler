package com.ahanaf.appscheduler.ui.home

import androidx.lifecycle.*
import com.ahanaf.appscheduler.application.MyApplication
import com.ahanaf.appscheduler.database.AppDatabase
import com.ahanaf.appscheduler.models.ScheduleAppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel:ViewModel() {

    val scheduleAppInfo: LiveData<List<ScheduleAppInfo>>
    private val repository :HomeRepository

    init {
        val dao = AppDatabase(MyApplication.myApplicationInstance!!).daoAccess()
        repository = HomeRepository(dao)
        scheduleAppInfo = repository.allSchedule
    }
}