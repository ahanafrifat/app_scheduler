package com.ahanaf.appscheduler.utils

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.TextView
import com.ahanaf.appscheduler.R
import com.google.android.material.snackbar.Snackbar
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object AppUtils {

    fun log(tag: String, msg: String) {
        Log.d(tag, msg)
    }

    fun message(
        view: View?,
        msg: String?,
        textColor: Int,
        backgroundColor: Int
    ) {
        try {
            if (view == null || msg == null) return
            val snack = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
            val snackBarView = snack.view
            snackBarView.setBackgroundColor(backgroundColor)
            val snackBarText = snackBarView.findViewById<TextView>(R.id.snackbar_text)
            snackBarText.setTextColor(textColor)
            snack.show()
        } catch (e: Exception) {
            return
        }
    }

    fun message(
        view: View?,
        msg: String?
    ) {
        try {
            if (view == null || msg == null) return
            val snack = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
            val snackBarView = snack.view
            snackBarView.setBackgroundColor(Color.GRAY)
            val snackBarText = snackBarView.findViewById<TextView>(R.id.snackbar_text)
            snackBarText.setTextColor(Color.WHITE)
            snack.show()
        } catch (e: Exception) {
            return
        }
    }

    fun millisLongToDateMonthYearTimeString(millis: Long): String {
        val d = Date()
        d.time = millis
        val dateFormat: DateFormat =
            SimpleDateFormat("dd MMM yyyy|h:mm a ", Locale.getDefault())
        return dateFormat.format(d)
    }

    fun dateToMillisecond(dateStr: String): Long {
        val dateFormat: DateFormat =
            SimpleDateFormat("yyyy-MM-dd , HH:mm:ss", Locale.getDefault())
        return try {
            dateFormat.parse(dateStr).time
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }
    }
}