package com.yogeshpaliyal.keypass

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.material.color.DynamicColors
import com.yogeshpaliyal.common.CommonMyApplication
import com.yogeshpaliyal.keypass.ui.CrashActivity
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlin.system.exitProcess

/*
* @author Yogesh Paliyal
* yogeshpaliyal.foss@gmail.com
* https://techpaliyal.com
* created on 22-01-2021 22:41
*/
@HiltAndroidApp
class MyApplication : CommonMyApplication() {

    override fun getCrashActivityIntent(throwable: Throwable): Intent {
        return CrashActivity.getIntent(this, throwable.localizedMessage)
    }

}
