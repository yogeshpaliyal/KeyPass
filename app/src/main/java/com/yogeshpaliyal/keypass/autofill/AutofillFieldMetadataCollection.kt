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
import android.view.autofill.AutofillId
import androidx.annotation.RequiresApi

/**
 * Data structure that stores a collection of `AutofillFieldMetadata`s. Contains all of the client's `View`
 * hierarchy autofill-relevant metadata.
 */
@RequiresApi(Build.VERSION_CODES.O)
data class AutofillFieldMetadataCollection @JvmOverloads constructor(
        val autofillIds: ArrayList<AutofillId> = ArrayList<AutofillId>(),
        val allAutofillHints: ArrayList<String> = ArrayList<String>(),
        val focusedAutofillHints: ArrayList<String> = ArrayList<String>()
) {

    private val autofillHintsToFieldsMap = HashMap<String, MutableList<AutofillFieldMetadata>>()
    var saveType = 0
        private set

    fun add(autofillFieldMetadata: AutofillFieldMetadata) {
        saveType = saveType or autofillFieldMetadata.saveType
        autofillFieldMetadata.autofillId?.let { autofillIds.add(it) }
        autofillFieldMetadata.autofillHints?.let {
            val hintsList = autofillFieldMetadata.autofillHints
            allAutofillHints.addAll(hintsList)
            if (autofillFieldMetadata.isFocused) {
                focusedAutofillHints.addAll(hintsList)
            }
            autofillFieldMetadata.autofillHints.forEach {
                val fields = autofillHintsToFieldsMap[it] ?: ArrayList<AutofillFieldMetadata>()
                autofillHintsToFieldsMap[it] = fields
                fields.add(autofillFieldMetadata)
            }
        }

    }

    fun getFieldsForHint(hint: String): MutableList<AutofillFieldMetadata>? {
        return autofillHintsToFieldsMap[hint]
    }
}
