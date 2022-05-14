/*
 * Copyright (C) 2018 The Android Open Source Project
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
package com.yogeshpaliyal.keypass.service

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.*
import android.util.ArrayMap
import android.util.Log
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.yogeshpaliyal.keypass.R
import java.util.*

/**
 * A very basic [AutofillService] implementation that only shows dynamic-generated datasets
 * and don't persist the saved data.
 *
 *
 * The goal of this class is to provide a simple autofill service implementation that is easy
 * to understand and extend, but it should **not** be used as-is on real apps because
 * it lacks fundamental security requirements such as data partitioning and package verification
 */
@RequiresApi(api = Build.VERSION_CODES.O)
class BasicService : AutofillService() {
    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        Log.d(TAG, "onFillRequest()")

        // Find autofillable fields
        val structure = getLatestAssistStructure(request)
        val fields = getAutofillableFields(structure)
        Log.d(TAG, "autofillable fields:$fields")
        if (fields.isEmpty()) {
            toast("No autofill hints found")
            callback.onSuccess(null)
            return
        }

        // Create the base response
        val response = FillResponse.Builder()

        // 1.Add the dynamic datasets
        val packageName = applicationContext.packageName
        for (i in 1..NUMBER_DATASETS) {
            val dataset = Dataset.Builder()
            for ((hint, id) in fields) {
                val value = "$i-$hint"
                // We're simple - our dataset values are hardcoded as "N-hint" (for example,
                // "1-username", "2-username") and they're displayed as such, except if they're a
                // password
                val displayValue = if (hint.contains("password")) "password for #$i" else value
                val presentation = newDatasetPresentation(packageName, displayValue)
                dataset.setValue(id!!, AutofillValue.forText(value), presentation)
            }
            response.addDataset(dataset.build())
        }

        // 2.Add save info
        val requiredIds = fields.values.toTypedArray()

        response.setSaveInfo( // We're simple, so we're generic
            SaveInfo.Builder(SaveInfo.SAVE_DATA_TYPE_GENERIC, requiredIds).build()
        )

        // 3.Profit!
        callback.onSuccess(response.build())
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        Log.d(TAG, "onSaveRequest()")
        toast("Save not supported")
        callback.onSuccess()
    }

    /**
     * Parses the [AssistStructure] representing the activity being autofilled, and returns a
     * map of autofillable fields (represented by their autofill ids) mapped by the hint associate
     * with them.
     *
     *
     */
    private fun getAutofillableFields(structure: AssistStructure): Map<String, AutofillId?> {
        val fields: MutableMap<String, AutofillId?> = ArrayMap()
        val nodes = structure.windowNodeCount
        for (i in 0 until nodes) {
            val node = structure.getWindowNodeAt(i).rootViewNode
            addAutofillableFields(fields, node)
        }
        return fields
    }

    /**
     * Adds any autofillable view from the [ViewNode] and its descendants to the map.
     */
    private fun addAutofillableFields(
        fields: MutableMap<String, AutofillId?>,
        node: ViewNode
    ) {
        val hints = node.autofillHints
        if (hints != null) {
            // We're simple, we only care about the first hint
            val hint = hints[0]?.lowercase()
            if (hint != null) {
                val id = node.autofillId
                if (!fields.containsKey(hint)) {
                    Log.v(TAG, "Setting hint '$hint' on $id")
                    fields[hint] = id
                } else {
                    Log.v(
                        TAG,
                        "Ignoring hint '" + hint + "' on " + id +
                            " because it was already set"
                    )
                }
            }
        }
        val childrenSize = node.childCount
        for (i in 0 until childrenSize) {
            addAutofillableFields(fields, node.getChildAt(i))
        }
    }

    /**
     * Displays a toast with the given message.
     */
    private fun toast(message: CharSequence) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val TAG = "BasicService"

        /**
         * Number of datasets sent on each request - we're simple, that value is hardcoded in our DNA!
         */
        private const val NUMBER_DATASETS = 4

        /**
         * Helper method to get the [AssistStructure] associated with the latest request
         * in an autofill context.
         */
        fun getLatestAssistStructure(request: FillRequest): AssistStructure {
            val fillContexts = request.fillContexts
            return fillContexts[fillContexts.size - 1].structure
        }

        /**
         * Helper method to create a dataset presentation with the given text.
         */
        fun newDatasetPresentation(
            packageName: String,
            text: CharSequence
        ): RemoteViews {
            val presentation = RemoteViews(packageName, R.layout.multidataset_service_list_item)
            presentation.setTextViewText(R.id.text, "paliyal" + text)
            presentation.setImageViewResource(R.id.icon, R.mipmap.ic_launcher)
            return presentation
        }
    }
}
