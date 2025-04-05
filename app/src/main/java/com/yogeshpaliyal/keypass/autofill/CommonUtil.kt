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

import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.Arrays

object CommonUtil {

    val TAG = "AutofillSample"

    val EXTRA_DATASET_NAME = "dataset_name"
    val EXTRA_FOR_RESPONSE = "for_response"

    private fun bundleToString(builder: StringBuilder, data: Bundle) {
        val keySet = data.keySet()
        builder.append("[Bundle with ").append(keySet.size).append(" keys:")
        for (key in keySet) {
            builder.append(' ').append(key).append('=')
            val value = data.get(key)
            if (value is Bundle) {
                bundleToString(builder, value)
            } else {
                val string = if (value is Array<*>) Arrays.toString(value) else value
                builder.append(string)
            }
        }
        builder.append(']')
    }

    fun bundleToString(data: Bundle?): String {
        if (data == null) {
            return "N/A"
        }
        val builder = StringBuilder()
        bundleToString(builder, data)
        return builder.toString()
    }

    fun createGson(): Gson {
        return GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create()
    }
}