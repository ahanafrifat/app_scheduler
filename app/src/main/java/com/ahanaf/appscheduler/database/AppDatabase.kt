package com.ahanaf.appscheduler.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ahanaf.appscheduler.models.ScheduleAppInfo
import com.ahanaf.appscheduler.utils.DATABASE_NAME

@Database(
    entities = [ScheduleAppInfo::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase(){

    abstract fun daoAccess(): IDaoAccess

    companion object{

        @Volatile private var instance: AppDatabase ?= null
        private val LOCK =Any ()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance?:buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

}