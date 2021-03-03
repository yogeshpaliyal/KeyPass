package com.yogeshpaliyal.keypass

import android.app.Application
import android.widget.Toast
import dagger.hilt.android.HiltAndroidApp

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 22-01-2021 22:41
*/
@HiltAndroidApp
class MyApplication : Application() {

    companion object {
        lateinit var instance: MyApplication
    }


    override fun onCreate() {
        super.onCreate()
        instance = this

    }

}
