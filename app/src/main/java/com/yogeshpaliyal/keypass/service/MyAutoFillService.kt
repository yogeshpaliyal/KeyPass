package com.yogeshpaliyal.keypass.service

import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import androidx.annotation.RequiresApi

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 15:25
*/
@RequiresApi(Build.VERSION_CODES.O)
class MyAutoFillService : AutofillService() {

    private val TAG = "MyAutoFillService"
    private val NUMBER_DATASETS = 4

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
    }
}
