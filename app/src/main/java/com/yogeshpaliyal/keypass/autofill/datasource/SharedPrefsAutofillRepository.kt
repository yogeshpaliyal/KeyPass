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
package com.yogeshpaliyal.keypass.autofill.datasource

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.ArraySet
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.yogeshpaliyal.keypass.autofill.CommonUtil
import com.yogeshpaliyal.keypass.autofill.model.FilledAutofillFieldCollection


/**
 * Singleton autofill data repository that stores autofill fields to SharedPreferences.
 * Disclaimer: you should not store sensitive fields like user data unencrypted. This is done
 * here only for simplicity and learning purposes.
 */
@RequiresApi(Build.VERSION_CODES.O)
object SharedPrefsAutofillRepository : AutofillRepository {
    private val SHARED_PREF_KEY = "com.example.android.autofillframework.service"
    private val CLIENT_FORM_DATA_KEY = "loginCredentialDatasets"
    private val DATASET_NUMBER_KEY = "datasetNumber"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
    }

    override fun getFilledAutofillFieldCollection(context: Context, focusedAutofillHints: List<String>,
                                                  allAutofillHints: List<String>): HashMap<String, FilledAutofillFieldCollection>? {
        var hasDataForFocusedAutofillHints = false
        val clientFormDataMap = HashMap<String, FilledAutofillFieldCollection>()
        val clientFormDataStringSet = getAllAutofillDataStringSet(context)
        val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create()
        val type = object : TypeToken<FilledAutofillFieldCollection>() {}.type
        for (clientFormDataString in clientFormDataStringSet) {
            gson.fromJson<FilledAutofillFieldCollection>(clientFormDataString, type)?.let {
                if (it.helpsWithHints(focusedAutofillHints)) {
                    // Saved data has data relevant to at least 1 of the hints associated with the
                    // View in focus.
                    hasDataForFocusedAutofillHints = true
                    it.datasetName?.let { datasetName ->
                        if (it.helpsWithHints(allAutofillHints)) {
                            // Saved data has data relevant to at least 1 of these hints associated with any
                            // of the Views in the hierarchy.
                            clientFormDataMap.put(datasetName, it)
                        }
                    }
                }
            }
        }
        if (hasDataForFocusedAutofillHints) {
            return clientFormDataMap
        } else {
            return null
        }
    }

    override fun saveFilledAutofillFieldCollection(context: Context, filledAutofillFieldCollection: FilledAutofillFieldCollection) {
        val datasetName = "dataset-" + getDatasetNumber(context)
        filledAutofillFieldCollection.datasetName = datasetName
        val allAutofillData = getAllAutofillDataStringSet(context)
        val gson = CommonUtil.createGson()
        allAutofillData.add(gson.toJson(filledAutofillFieldCollection).toString())
        saveAllAutofillDataStringSet(context, allAutofillData)
        incrementDatasetNumber(context)
    }

    override fun clear(context: Context) {
        getPrefs(context).edit().remove(CLIENT_FORM_DATA_KEY).remove(DATASET_NUMBER_KEY).apply()
    }

    private fun getAllAutofillDataStringSet(context: Context): MutableSet<String> {
        return getPrefs(context).getStringSet(CLIENT_FORM_DATA_KEY, ArraySet<String>())
    }

    private fun saveAllAutofillDataStringSet(context: Context,
            allAutofillDataStringSet: Set<String>) {
        getPrefs(context).edit().putStringSet(CLIENT_FORM_DATA_KEY, allAutofillDataStringSet).apply()
    }

    /**
     * For simplicity, datasets will be named in the form "dataset-X" where X means
     * this was the Xth dataset saved.
     */
    private fun getDatasetNumber(context: Context): Int {
        return getPrefs(context).getInt(DATASET_NUMBER_KEY, 0)
    }

    /**
     * Every time a dataset is saved, this should be called to increment the dataset number.
     * (only important for this service's dataset naming scheme).
     */
    private fun incrementDatasetNumber(context: Context) {
        getPrefs(context).edit().putInt(DATASET_NUMBER_KEY, getDatasetNumber(context) + 1).apply()
    }
}