package com.ahanaf.appscheduler.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(indices = [Index(value = ["id","time"], unique = true)])
data class ScheduleAppInfo(
    val name: String?,
    val packageName: String?,
    var time: Long?,
    val isAlarmActive: Boolean?,
    val scheduleId: String?
):Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScheduleAppInfo

        if (name != other.name) return false
        if (packageName != other.packageName) return false
        if (time != other.time) return false
        if (isAlarmActive != other.isAlarmActive) return false
        if (scheduleId != other.scheduleId) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (packageName?.hashCode() ?: 0)
        result = 31 * result + (time?.hashCode() ?: 0)
        result = 31 * result + (isAlarmActive?.hashCode() ?: 0)
        result = 31 * result + (scheduleId?.hashCode() ?: 0)
        result = 31 * result + id
        return result
    }

}