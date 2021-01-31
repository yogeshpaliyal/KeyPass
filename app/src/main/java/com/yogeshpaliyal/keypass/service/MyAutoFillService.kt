package com.yogeshpaliyal.keypass.service

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.*
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import androidx.annotation.NonNull
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