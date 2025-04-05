package com.yogeshpaliyal.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.autofill.AutofillManager
import androidx.annotation.RequiresApi
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

@RequiresApi(Build.VERSION_CODES.O)
fun Context?.isAutoFillServiceEnabled(): Boolean {
    val autofillManager = this?.getAutoFillService()
    return autofillManager?.hasEnabledAutofillServices() == true
}

fun Context?.enableAutoFillService() {
    if (this != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val intent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE)
        intent.setData(Uri.parse("package:com.yogeshpaliyal.keypass"));

//        intent.putExtra(Settings.EXTRA_AUTOFILL_SERVICE_COMPONENT_NAME, "com.yogeshpaliyal.keypass/.autofill.KeyPassAutofillService")
        this.startActivity(intent)
    }
}
