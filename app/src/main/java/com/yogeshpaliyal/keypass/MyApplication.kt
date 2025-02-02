package com.yogeshpaliyal.keypass

import android.content.Intent
import android.os.SystemClock
import com.yogeshpaliyal.common.CommonMyApplication
import com.yogeshpaliyal.keypass.ui.CrashActivity
import dagger.hilt.android.HiltAndroidApp

/*
* @author Yogesh Paliyal
* yogeshpaliyal.foss@gmail.com
* https://techpaliyal.com
* created on 22-01-2021 22:41
*/
@HiltAndroidApp
class MyApplication : CommonMyApplication() {

    private var timeToLaunchActivity : Long? = null

    fun activityLaunchTriggered() {
        timeToLaunchActivity = SystemClock.uptimeMillis()
    }

    fun isActivityLaunchTriggered() : Boolean {
        val mTimeToLaunchActivity = timeToLaunchActivity ?: return false
        timeToLaunchActivity = null
        return SystemClock.uptimeMillis() - mTimeToLaunchActivity < 1000
    }

    override fun getCrashActivityIntent(throwable: Throwable): Intent {
        return CrashActivity.getIntent(this, throwable.stackTraceToString())
    }
}
