package com.yogeshpaliyal.common.utils

import android.content.Context
import android.view.autofill.AutofillManager
import androidx.core.content.ContextCompat.getSystemService

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 15:27
*/

fun Context?.getAutoFillService() = if (this != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
    getSystemService(this, AutofillManager::class.java)
} else {
    null
}
