package com.ahanaf.appscheduler.ui.home

import androidx.lifecycle.LiveData
import com.ahanaf.appscheduler.database.IDaoAccess
import com.ahanaf.appscheduler.models.ScheduleAppInfo

class HomeRepository (private val iDaoAccess: IDaoAccess){

    val allSchedule : LiveData<List<ScheduleAppInfo>> = iDaoAccess.getAllScheduleLiveData()

}