package com.yogeshpaliyal.keypasscompose

import android.content.Intent
import com.yogeshpaliyal.common.CommonMyApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: CommonMyApplication() {
    override fun getCrashActivityIntent(throwable: Throwable): Intent? {
        return null
    }
}