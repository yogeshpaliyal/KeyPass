/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yogeshpaliyal.keypass.autofill

import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.yogeshpaliyal.keypass.autofill.datasource.SharedPrefsAutofillRepository
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.autofill.CommonUtil.TAG
import com.yogeshpaliyal.keypass.autofill.CommonUtil.bundleToString
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class KeyPassAutofillService : AutofillService() {

    @Inject
    lateinit var sharedPrefsAutofillRepository: SharedPrefsAutofillRepository

    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal,
            callback: FillCallback) {
        val structure = request.fillContexts[request.fillContexts.size - 1].structure
        val packageName = structure.activityComponent.packageName
        if (!PackageVerifier.isValidPackage(applicationContext, packageName)) {
            Toast.makeText(applicationContext, R.string.invalid_package_signature,
                    Toast.LENGTH_SHORT).show()
            return
        }
        val data = request.clientState
        Log.d(TAG, "onFillRequest(): data=" + bundleToString(data))
        cancellationSignal.setOnCancelListener { Log.w(TAG, "Cancel autofill not implemented in this sample.") }
        // Parse AutoFill data in Activity
        val parser = StructureParser(structure)
        parser.parseForFill()
        val autofillFields = parser.autofillFields

//        val responseBuilder = FillResponse.Builder()
        // Check user's settings for authenticating Responses and Datasets.
        val responseAuth = false // MyPreferences.isResponseAuth(this)
        if (responseAuth && autofillFields.autofillIds.size > 0) {
            // If the entire Autofill Response is authenticated, AuthActivity is used
            // to generate Response.
//            val sender = AuthActivity.getAuthIntentSenderForResponse(this)
//            val presentation = AutofillHelper
//                    .newRemoteViews(packageName, getString(R.string.autofill_sign_in_prompt), R.drawable.ic_lock_black_24dp)
//            responseBuilder
//                    .setAuthentication(autofillFields.autofillIds.toTypedArray(), sender, presentation)
//            callback.onSuccess(responseBuilder.build())
        } else {
//            val datasetAuth = MyPreferences.isDatasetAuth(this)
            val clientFormDataMap = sharedPrefsAutofillRepository.getFilledAutofillFieldCollection(structure.activityComponent.packageName,
                    autofillFields.focusedAutofillHints, autofillFields.allAutofillHints)
            val response = AutofillHelper.newResponse(this, false, autofillFields, clientFormDataMap)
            callback.onSuccess(response)
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        val context = request.fillContexts
        val structure = context[context.size - 1].structure
        val packageName = structure.activityComponent.packageName
        if (!PackageVerifier.isValidPackage(applicationContext, packageName)) {
            Toast.makeText(applicationContext, R.string.invalid_package_signature,
                    Toast.LENGTH_SHORT).show()
            return
        }
        val data = request.clientState
//        Log.d(TAG, "onSaveRequest(): data=" + bundleToString(data))
        val parser = StructureParser(structure)
        parser.parseForSave()
        sharedPrefsAutofillRepository.saveFilledAutofillFieldCollection(parser.filledAutofillFieldCollection, structure.activityComponent.packageName)
    }

    override fun onConnected() {
        Log.d(TAG, "onConnected")
    }

    override fun onDisconnected() {
        Log.d(TAG, "onDisconnected")
    }
}
