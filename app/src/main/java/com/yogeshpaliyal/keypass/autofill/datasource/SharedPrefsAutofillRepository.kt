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
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.yogeshpaliyal.common.AppDatabase
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.autofill.model.FilledAutofillFieldCollection
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


/**
 * Singleton autofill data repository that stores autofill fields to SharedPreferences.
 * Disclaimer: you should not store sensitive fields like user data unencrypted. This is done
 * here only for simplicity and learning purposes.
 */
@RequiresApi(Build.VERSION_CODES.O)
class SharedPrefsAutofillRepository @Inject constructor(private val database: AppDatabase) : AutofillRepository {

    override fun getFilledAutofillFieldCollection(packageName: String, focusedAutofillHints: List<String>,
                                                  allAutofillHints: List<String>): HashMap<String, FilledAutofillFieldCollection>? {
        var hasDataForFocusedAutofillHints = false
        val clientFormDataMap = HashMap<String, FilledAutofillFieldCollection>()
        val clientFormDataStringSet = getAllAutofillDataStringSet(packageName)
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

    override fun saveFilledAutofillFieldCollection(filledAutofillFieldCollection: FilledAutofillFieldCollection, site: String) {
        //filledAutofillFieldCollection.datasetName = datasetName
        var userName = filledAutofillFieldCollection.hintMap[View.AUTOFILL_HINT_USERNAME]?.textValue
        if (userName == null) {
            userName = filledAutofillFieldCollection.hintMap[View.AUTOFILL_HINT_EMAIL_ADDRESS]?.textValue
        }
        if (userName == null) {
            userName = filledAutofillFieldCollection.hintMap[View.AUTOFILL_HINT_PHONE]?.textValue
        }
        val accountModel = AccountModel(
                title = userName,
                username = userName,
                password = filledAutofillFieldCollection.hintMap[View.AUTOFILL_HINT_PASSWORD]?.textValue,
                site = site
        )
        runBlocking {
            database.getDao().insertOrUpdateAccount(accountModel)
        }
    }

    override fun clear(context: Context) {
        // NO-OP
    }

    private fun getAllAutofillDataStringSet(packageName: String): List<String> {
        return runBlocking {
            return@runBlocking database.getDao().getAllAccountsListByPackageName(packageName).map {account ->
             val jsonObject = JsonObject()
                jsonObject.addProperty("datasetName", account.title ?: "")
                val hintMap = JsonObject()
                hintMap.add(View.AUTOFILL_HINT_USERNAME, JsonObject().also {
                    it.addProperty("textValue", account.username)
                })
                hintMap.add(View.AUTOFILL_HINT_PASSWORD, JsonObject().also {
                    it.addProperty("textValue", account.password)
                })
                hintMap.add(View.AUTOFILL_HINT_EMAIL_ADDRESS, JsonObject().also {
                    it.addProperty("textValue", account.username)
                })
                hintMap.add(View.AUTOFILL_HINT_PHONE, JsonObject().also {
                    it.addProperty("textValue", account.username)
                })
                jsonObject.add("hintMap", hintMap)
                jsonObject.toString()
         }
        }
    }
}