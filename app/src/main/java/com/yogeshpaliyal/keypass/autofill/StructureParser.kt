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
import android.text.InputType
import android.util.Log
import android.view.View
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
    var webDomain: String? = null
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
        val explicitHints = viewNode.autofillHints
        if (explicitHints != null && explicitHints.isNotEmpty()) {
            if (forFill) {
                autofillFields.add(AutofillFieldMetadata(viewNode))
            } else {
                filledAutofillFieldCollection.add(FilledAutofillField(viewNode))
            }
        } else {
            val inferredHints = inferAutofillHints(viewNode)
            if (inferredHints != null && inferredHints.isNotEmpty()) {
                if (forFill) {
                    autofillFields.add(AutofillFieldMetadata(viewNode, inferredHints))
                } else {
                    filledAutofillFieldCollection.add(FilledAutofillField(viewNode, inferredHints))
                }
            }
        }

        viewNode.webDomain?.let { domain ->
            if (domain.isNotEmpty()) {
                webDomain = domain
            }
        }

        val childrenSize = viewNode.childCount
        for (i in 0 until childrenSize) {
            parseLocked(forFill, viewNode.getChildAt(i))
        }
    }

    /**
     * Infers autofill hints from a ViewNode's inputType, HTML attributes, hint text,
     * and id when explicit autofillHints are not set by the app.
     */
    private fun inferAutofillHints(viewNode: ViewNode): Array<String>? {
        if (viewNode.autofillType == View.AUTOFILL_TYPE_NONE) return null

        val hints = mutableListOf<String>()

        // 1. Check inputType
        val inputType = viewNode.inputType
        if (inputType > 0) {
            val inputTypeClass = inputType and InputType.TYPE_MASK_CLASS
            val inputTypeVariation = inputType and InputType.TYPE_MASK_VARIATION
            when (inputTypeClass) {
                InputType.TYPE_CLASS_TEXT -> {
                    when (inputTypeVariation) {
                        InputType.TYPE_TEXT_VARIATION_PASSWORD,
                        InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD,
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ->
                            hints.add(View.AUTOFILL_HINT_PASSWORD)
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                        InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS ->
                            hints.add(View.AUTOFILL_HINT_EMAIL_ADDRESS)
                    }
                }
                InputType.TYPE_CLASS_PHONE -> {
                    hints.add(View.AUTOFILL_HINT_PHONE)
                }
                InputType.TYPE_CLASS_NUMBER -> {
                    if (inputTypeVariation == InputType.TYPE_NUMBER_VARIATION_PASSWORD) {
                        hints.add(View.AUTOFILL_HINT_PASSWORD)
                    }
                }
            }
        }

        // 2. Check HTML info (for WebViews/browsers)
        if (hints.isEmpty()) {
            viewNode.htmlInfo?.let { htmlInfo ->
                val attrs = htmlInfo.attributes
                if (attrs != null) {
                    val typeAttr = attrs.firstOrNull {
                        it.first.equals("type", ignoreCase = true)
                    }?.second?.lowercase()
                    when (typeAttr) {
                        "password" -> hints.add(View.AUTOFILL_HINT_PASSWORD)
                        "email" -> hints.add(View.AUTOFILL_HINT_EMAIL_ADDRESS)
                        "tel" -> hints.add(View.AUTOFILL_HINT_PHONE)
                        "text", "url", null -> {
                            val nameAttr = attrs.firstOrNull {
                                it.first.equals("name", ignoreCase = true) ||
                                    it.first.equals("id", ignoreCase = true)
                            }?.second?.lowercase() ?: ""
                            when {
                                nameAttr.contains("pass") ->
                                    hints.add(View.AUTOFILL_HINT_PASSWORD)
                                nameAttr.contains("user") || nameAttr.contains("login") ->
                                    hints.add(View.AUTOFILL_HINT_USERNAME)
                                nameAttr.contains("email") ->
                                    hints.add(View.AUTOFILL_HINT_EMAIL_ADDRESS)
                            }
                        }
                    }
                }
            }
        }

        // 3. Check hint text and idEntry for common patterns
        if (hints.isEmpty()) {
            val hintText = viewNode.hint?.toString()?.lowercase() ?: ""
            val idEntry = viewNode.idEntry?.lowercase() ?: ""
            val combined = "$hintText $idEntry"

            when {
                combined.contains("password") || combined.contains("passwd") ->
                    hints.add(View.AUTOFILL_HINT_PASSWORD)
                combined.contains("username") || combined.contains("user_name") || combined.contains("login") ->
                    hints.add(View.AUTOFILL_HINT_USERNAME)
                combined.contains("email") || combined.contains("e-mail") ->
                    hints.add(View.AUTOFILL_HINT_EMAIL_ADDRESS)
                combined.contains("phone") || combined.contains("tel") ->
                    hints.add(View.AUTOFILL_HINT_PHONE)
            }
        }

        return if (hints.isNotEmpty()) hints.toTypedArray() else null
    }
}
