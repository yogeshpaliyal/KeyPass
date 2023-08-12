package com.yogeshpaliyal.common

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.material.color.DynamicColors
import javax.inject.Inject
import kotlin.system.exitProcess

abstract class CommonMyApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    abstract fun getCrashActivityIntent(throwable: Throwable): Intent

    override fun onCreate() {
        super.onCreate()

        val previewExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val intent = getCrashActivityIntent(throwable)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            previewExceptionHandler?.uncaughtException(thread, throwable)
        }

        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()
    }
}
