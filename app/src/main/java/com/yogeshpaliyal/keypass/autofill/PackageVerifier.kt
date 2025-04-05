package com.yogeshpaliyal.keypass.autofill

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

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.yogeshpaliyal.keypass.autofill.CommonUtil.TAG
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

object PackageVerifier {


    /**
     * Verifies if a package is valid by matching its certificate with the previously stored
     * certificate.
     */
    fun isValidPackage(context: Context, packageName: String): Boolean {
        val hash: String
        try {
            hash = getCertificateHash(context, packageName)
            Log.d(TAG, "Hash for $packageName: $hash")
        } catch (e: Exception) {
            Log.w(TAG, "Error getting hash for $packageName: $e")
            return false
        }

        return verifyHash(context, packageName, hash)
    }

    private fun getCertificateHash(context: Context, packageName: String): String {
        val pm = context.packageManager
        val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        val signatures = packageInfo.signatures
        val cert = signatures?.get(0)?.toByteArray()
        ByteArrayInputStream(cert).use { input ->
            val factory = CertificateFactory.getInstance("X509")
            val x509 = factory.generateCertificate(input) as X509Certificate
            val md = MessageDigest.getInstance("SHA256")
            val publicKey = md.digest(x509.encoded)
            return toHexFormat(publicKey)
        }
    }

    private fun toHexFormat(bytes: ByteArray): String {
        val builder = StringBuilder(bytes.size * 2)
        for (i in bytes.indices) {
            var hex = Integer.toHexString(bytes[i].toInt())
            val length = hex.length
            if (length == 1) {
                hex = "0" + hex
            }
            if (length > 2) {
                hex = hex.substring(length - 2, length)
            }
            builder.append(hex.toUpperCase())
            if (i < bytes.size - 1) {
                builder.append(':')
            }
        }
        return builder.toString()
    }

    private fun verifyHash(context: Context, packageName: String, hash: String): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(
                "package-hashes", Context.MODE_PRIVATE)
        if (!prefs.contains(packageName)) {
            Log.d(TAG, "Creating intial hash for " + packageName)
            prefs.edit().putString(packageName, hash).apply()
            return true
        }

        val existingHash = prefs.getString(packageName, null)
        if (hash != existingHash) {
            Log.w(TAG, "hash mismatch for " + packageName + ": expected " + existingHash
                    + ", got " + hash)
            return false
        }
        return true
    }
}