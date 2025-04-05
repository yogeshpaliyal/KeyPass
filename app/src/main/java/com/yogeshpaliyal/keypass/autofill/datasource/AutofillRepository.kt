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
import com.yogeshpaliyal.keypass.autofill.model.FilledAutofillFieldCollection
import java.util.HashMap

interface AutofillRepository {

    /**
     * Gets saved FilledAutofillFieldCollection that contains some objects that can autofill fields with these
     * `autofillHints`.
     */
    fun getFilledAutofillFieldCollection(context: Context, focusedAutofillHints: List<String>,
            allAutofillHints: List<String>): HashMap<String, FilledAutofillFieldCollection>?

    /**
     * Saves LoginCredential under this datasetName.
     */
    fun saveFilledAutofillFieldCollection(context: Context,
            filledAutofillFieldCollection: FilledAutofillFieldCollection)

    /**
     * Clears all data.
     */
    fun clear(context: Context)
}
