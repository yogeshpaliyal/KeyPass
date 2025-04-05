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
package com.yogeshpaliyal.keypass.autofill.model

import android.app.assist.AssistStructure
import android.os.Build
import android.view.autofill.AutofillValue
import androidx.annotation.RequiresApi
import com.google.gson.annotations.Expose
import com.yogeshpaliyal.keypass.autofill.AutofillHelper

/**
 * JSON serializable data class containing the same data as an [AutofillValue].
 */
@RequiresApi(Build.VERSION_CODES.O)
class FilledAutofillField(viewNode: AssistStructure.ViewNode) {
    @Expose
    var textValue: String? = null

    @Expose
    var dateValue: Long? = null

    @Expose
    var toggleValue: Boolean? = null

    val autofillHints = viewNode.autofillHints?.filter(AutofillHelper::isValidHint)?.toTypedArray()

    init {
        viewNode.autofillValue?.let {
            if (it.isList) {
                val index = it.listValue
                viewNode.autofillOptions?.let { autofillOptions ->
                    if (autofillOptions.size > index) {
                        textValue = autofillOptions[index].toString()
                    }
                }
            } else if (it.isDate) {
                dateValue = it.dateValue
            } else if (it.isText) {
                // Using toString of AutofillValue.getTextValue in order to save it to
                // SharedPreferences.
                textValue = it.textValue.toString()
            } else {
            }
        }
    }

    fun isNull(): Boolean {
        return textValue == null && dateValue == null && toggleValue == null
    }
}
