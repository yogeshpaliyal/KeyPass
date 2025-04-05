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

import android.content.Context
import android.os.Build
import android.service.autofill.Dataset
import android.service.autofill.FillResponse
import android.service.autofill.SaveInfo
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.autofill.CommonUtil.TAG
import com.yogeshpaliyal.keypass.autofill.model.FilledAutofillFieldCollection
import java.util.HashMap


/**
 * This is a class containing helper methods for building Autofill Datasets and Responses.
 */
@RequiresApi(Build.VERSION_CODES.O)
object AutofillHelper {

    /**
     * Wraps autofill data in a [Dataset] object which can then be sent back to the
     * client View.
     */
    fun newDataset(context: Context, autofillFieldMetadata: AutofillFieldMetadataCollection,
                   filledAutofillFieldCollection: FilledAutofillFieldCollection,
                   datasetAuth: Boolean): Dataset? {
        filledAutofillFieldCollection.datasetName?.let { datasetName ->
            val datasetBuilder: Dataset.Builder
            if (datasetAuth) {
                datasetBuilder = Dataset.Builder(newRemoteViews(context.packageName, datasetName,
                        R.drawable.ic_person_black_24dp))
                // TODO: Uncomment this when authentication is implemented
//                val sender = AuthActivity.getAuthIntentSenderForDataset(context, datasetName)
//                datasetBuilder.setAuthentication(sender)
            } else {
                datasetBuilder = Dataset.Builder(newRemoteViews(context.packageName, datasetName,
                        R.drawable.ic_person_black_24dp))
            }
            val setValueAtLeastOnce = filledAutofillFieldCollection
                    .applyToFields(autofillFieldMetadata, datasetBuilder)
            if (setValueAtLeastOnce) {
                return datasetBuilder.build()
            }
        }
        return null
    }

    fun newRemoteViews(packageName: String, remoteViewsText: String,
            @DrawableRes drawableId: Int): RemoteViews {
        val presentation = RemoteViews(packageName, R.layout.multidataset_service_list_item)
        presentation.setTextViewText(R.id.text, remoteViewsText)
        presentation.setImageViewResource(R.id.icon, drawableId)
        return presentation
    }

    /**
     * Wraps autofill data in a [FillResponse] object (essentially a series of Datasets) which can
     * then be sent back to the client View.
     */
    fun newResponse(context: Context,
            datasetAuth: Boolean, autofillFieldMetadata: AutofillFieldMetadataCollection,
            filledAutofillFieldCollectionMap: HashMap<String, FilledAutofillFieldCollection>?): FillResponse? {
        val responseBuilder = FillResponse.Builder()
        filledAutofillFieldCollectionMap?.keys?.let { datasetNames ->
            for (datasetName in datasetNames) {
                filledAutofillFieldCollectionMap[datasetName]?.let { clientFormData ->
                    val dataset = newDataset(context, autofillFieldMetadata, clientFormData, datasetAuth)
                    dataset?.let(responseBuilder::addDataset)
                }
            }
        }
        if (autofillFieldMetadata.saveType != 0) {
            val autofillIds = autofillFieldMetadata.autofillIds
            responseBuilder.setSaveInfo(SaveInfo.Builder(autofillFieldMetadata.saveType,
                    autofillIds.toTypedArray()).build())
            return responseBuilder.build()
        } else {
            Log.d(TAG, "These fields are not meant to be saved by autofill.")
            return null
        }
    }

    fun isValidHint(hint: String): Boolean {
        when (hint) {
            View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE,
            View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DAY,
            View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_MONTH,
            View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_YEAR,
            View.AUTOFILL_HINT_CREDIT_CARD_NUMBER,
            View.AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE,
            View.AUTOFILL_HINT_EMAIL_ADDRESS,
            View.AUTOFILL_HINT_PHONE,
            View.AUTOFILL_HINT_NAME,
            View.AUTOFILL_HINT_PASSWORD,
            View.AUTOFILL_HINT_POSTAL_ADDRESS,
            View.AUTOFILL_HINT_POSTAL_CODE,
            View.AUTOFILL_HINT_USERNAME ->
                return true
            else ->
                return false
        }
    }
}