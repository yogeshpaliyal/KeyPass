package com.yogeshpaliyal.keypass.utils

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.yogeshpaliyal.keypass.MyApplication
import java.util.*

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 21-02-2021 11:18
*/

fun getSharedPreferences(): SharedPreferences {
    val masterKeyAlias = MasterKey.Builder(MyApplication.instance).also {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // this is equivalent to using deprecated MasterKeys.AES256_GCM_SPEC

            // this is equivalent to using deprecated MasterKeys.AES256_GCM_SPEC
            val spec = KeyGenParameterSpec.Builder(
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
            it.setKeyGenParameterSpec(spec)
        }
        // it.setUserAuthenticationRequired(true)
    }
        .build()

    return EncryptedSharedPreferences.create(
        MyApplication.instance,
        "secret_shared_prefs",
        masterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}

/**
 * Pair
 * 1st => true if key is created now & false if key is created previously
 *
 */
fun getOrCreateBackupKey(reset: Boolean = false): Pair<Boolean, String> {
    val sp = getSharedPreferences()

    return if (sp.contains(BACKUP_KEY) && reset.not()) {
        Pair(false, sp.getString(BACKUP_KEY, "") ?: "")
    } else {
        val randomKey = getRandomString(16)
        sp.edit {
            putString(BACKUP_KEY, randomKey)
        }
        Pair(true, randomKey)
    }
}

fun setBackupDirectory(string: String) {
    getSharedPreferences().edit {
        putString(BACKUP_DIRECTORY, string)
    }
}

fun getBackupDirectory(): String {
    val sp = getSharedPreferences()
    return sp.getString(BACKUP_DIRECTORY, "") ?: ""
}

private const val BACKUP_KEY = "backup_key"
private const val BACKUP_DIRECTORY = "backup_directory"
