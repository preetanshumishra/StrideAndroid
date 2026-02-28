package com.preetanshumishra.stride

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.preetanshumishra.stride.di.AppDependencies
import com.preetanshumishra.stride.di.DaggerAppComponent
import com.preetanshumishra.stride.di.initializeAppDependencies
import com.preetanshumishra.stride.utils.WoosmapManager

class StrideApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val appComponent = DaggerAppComponent
            .builder()
            .context(this)
            .build()

        val appDependencies = AppDependencies(appComponent)
        initializeAppDependencies(appDependencies)

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            WoosmapManager.CHANNEL_ID,
            "Stride Geofence Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications when you are near your saved places"
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
