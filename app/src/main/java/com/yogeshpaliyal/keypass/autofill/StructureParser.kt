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

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.yogeshpaliyal.keypass.autofill.CommonUtil.TAG
import com.yogeshpaliyal.keypass.autofill.model.FilledAutofillField
import com.yogeshpaliyal.keypass.autofill.model.FilledAutofillFieldCollection

/**
 * Parser for an AssistStructure object. This is invoked when the Autofill Service receives an
 * AssistStructure from the client Activity, representing its View hierarchy. In this sample, it
 * parses the hierarchy and collects autofill metadata from {@link ViewNode}s along the way.
 */
@RequiresApi(Build.VERSION_CODES.O)
internal class StructureParser(private val autofillStructure: AssistStructure) {
    val autofillFields = AutofillFieldMetadataCollection()
    var filledAutofillFieldCollection: FilledAutofillFieldCollection = FilledAutofillFieldCollection()
        private set

    fun parseForFill() {
        parse(true)
    }

    fun parseForSave() {
        parse(false)
    }

    /**
     * Traverse AssistStructure and add ViewNode metadata to a flat list.
     */
    private fun parse(forFill: Boolean) {
        Log.d(TAG, "Parsing structure for " + autofillStructure.activityComponent)
        val nodes = autofillStructure.windowNodeCount
        filledAutofillFieldCollection = FilledAutofillFieldCollection()
        for (i in 0 until nodes) {
            parseLocked(forFill, autofillStructure.getWindowNodeAt(i).rootViewNode)
        }
    }

    private fun parseLocked(forFill: Boolean, viewNode: ViewNode) {
        viewNode.autofillHints?.let { autofillHints ->
            if (autofillHints.isNotEmpty()) {
                if (forFill) {
                    autofillFields.add(AutofillFieldMetadata(viewNode))
                } else {
                    filledAutofillFieldCollection.add(FilledAutofillField(viewNode))
                }
            }
        }
        val childrenSize = viewNode.childCount
        for (i in 0 until childrenSize) {
            parseLocked(forFill, viewNode.getChildAt(i))

        }
    }
}
