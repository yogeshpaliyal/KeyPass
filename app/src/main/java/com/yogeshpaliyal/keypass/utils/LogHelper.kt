package com.yogeshpaliyal.keypass.utils

import android.util.Log
import com.yogeshpaliyal.keypass.BuildConfig

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 26-12-2020 20:21
*/

@JvmName("LogHelper")

fun Any?.systemOutPrint() {
    if (BuildConfig.DEBUG) println(this)
}

fun Any?.systemErrPrint() {
    if (BuildConfig.DEBUG) System.err.println(this)
}

fun Exception?.debugPrintStackTrace() {
    if (BuildConfig.DEBUG) this?.printStackTrace()
}

fun Throwable?.debugPrintStackTrace() {
    if (BuildConfig.DEBUG) this?.printStackTrace()
}

fun Any?.logD(tag: String?) {
    if (BuildConfig.DEBUG) Log.d(tag, this.toString())
}

fun Any?.logE(tag: String?) {
    if (BuildConfig.DEBUG) Log.e(tag, this.toString())
}

fun Any?.logI(tag: String?) {
    if (BuildConfig.DEBUG) Log.i(tag, this.toString())
}

fun Any?.logV(tag: String?) {
    if (BuildConfig.DEBUG) Log.v(tag, this.toString())
}

fun Any?.logW(tag: String?) {
    if (BuildConfig.DEBUG) Log.w(tag, this.toString())
}
