package com.ahanaf.appscheduler.application

import android.app.Application

class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        myApplicationInstance = this
    }

    companion object {
        var myApplicationInstance: MyApplication? = null
            private set
    }
}