package com.preetanshumishra.stride

import android.app.Application
import com.preetanshumishra.stride.di.AppDependencies
import com.preetanshumishra.stride.di.DaggerAppComponent
import com.preetanshumishra.stride.di.initializeAppDependencies

class StrideApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val appComponent = DaggerAppComponent
            .builder()
            .context(this)
            .build()

        val appDependencies = AppDependencies(appComponent)
        initializeAppDependencies(appDependencies)
    }
}
