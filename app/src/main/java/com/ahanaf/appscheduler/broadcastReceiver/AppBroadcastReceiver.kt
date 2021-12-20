package com.ahanaf.appscheduler.broadcastReceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ahanaf.appscheduler.ui.MainActivity
import com.ahanaf.appscheduler.R
import java.util.*

class AppBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals("com.ahanaf.appscheduler")) {
            var bundle = intent.extras
            var appName = bundle?.getString("app_name")
            var appPackageName = bundle?.getString("app_package_name")
            Log.d("TEST_BROADCAST", "action bundle: " + bundle?.getString("app_package_name"))

            if(!appName.isNullOrEmpty() && !appPackageName.isNullOrEmpty()){
                showNotification(context, appName, appPackageName)
                val packageManager = context?.packageManager
                context.startActivity(packageManager?.getLaunchIntentForPackage(appPackageName.toString()))
                startApp(context, appName, appPackageName)
            }
//            startActivity(context.packageManager?.getLaunchIntentForPackage(bundle?.getString("app_package_name")))
        }
        Log.d("TEST_BROADCAST", "action: " + intent.action)
    }

    private fun startApp(context: Context, appName: String, appPackageName: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("app_name", appName)
        intent.putExtra("app_package_name", appPackageName)
        context.startActivity(intent)
        Log.d("TEST_BROADCAST", "startApp" )
    }

    private fun showNotification(context: Context, appName: String, appPackageName: String) {

        val CHANNEL_ID = UUID.randomUUID().toString()
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("app_name", appName)
        intent.putExtra("app_package_name", appPackageName)
        val pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle(context.resources.getString(R.string.app_name))
            setContentText(appName)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(defaultSoundUri)
                .setFullScreenIntent(pendingIntent, true)
                .setContentIntent(pendingIntent)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Channel human readable title", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 , notificationBuilder.build())
    }


}