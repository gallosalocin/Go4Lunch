package com.gallosalocin.go4lunch

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import com.gallosalocin.go4lunch.util.Constants.CHANNEL_MY_LUNCH_NOTIFICATION_ID
import com.google.android.libraries.places.api.Places
import timber.log.Timber
import timber.log.Timber.DebugTree

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.ApiKey)
        }
        applicationOrientation()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    CHANNEL_MY_LUNCH_NOTIFICATION_ID,
                    getString(R.string.notification_channel_title),
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = getString(R.string.notification_channel_description)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun applicationOrientation() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}