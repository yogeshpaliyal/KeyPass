package com.yogeshpaliyal.common.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 21-02-2021 11:18
*/

val Context.dataStore by preferencesDataStore(
    name = "settings"
)

const val BACKUP_KEY_LENGTH = 16

/**
 * Pair
 * 1st => true if key is created now & false if key is created previously
 *
 */
suspend fun Context.getOrCreateBackupKey(reset: Boolean = false): Pair<Boolean, String> {
    val sp = dataStore.data.first()
    return if (sp.contains(BACKUP_KEY) && reset.not()) {
        Pair(false, (sp[BACKUP_KEY]) ?: "")
    } else {
        val randomKey = getRandomString(BACKUP_KEY_LENGTH)
        dataStore.edit {
            it[BACKUP_KEY] = randomKey
        }
        Pair(true, randomKey)
    }
}

suspend fun Context.getKeyPassPassword(): String? {
    return dataStore.data.first().get(KEYPASS_PASSWORD)
}

suspend fun Context.setKeyPassPassword(password: String?) {
    dataStore.edit {
        if (password == null) {
            it.remove(KEYPASS_PASSWORD)
        } else {
            it[KEYPASS_PASSWORD] = password
        }
    }
}

suspend fun Context.isKeyPresent(): Boolean {
    val sp = dataStore.data.first()
    return sp.contains(BACKUP_KEY)
}

suspend fun Context.saveKeyphrase(keyphrase: String) {
    dataStore.edit {
        it[BACKUP_KEY] = keyphrase
    }
}

suspend fun Context?.clearBackupKey() {
    this?.dataStore?.edit {
        it.remove(BACKUP_KEY)
    }
}

suspend fun Context?.setBackupDirectory(string: String) {
    this?.dataStore?.edit {
        it[BACKUP_DIRECTORY] = string
    }
}

suspend fun Context?.setBackupTime(time: Long) {
    this?.dataStore?.edit {
        it[BACKUP_DATE_TIME] = time
    }
}

suspend fun Context?.getBackupDirectory(): String {
    return this?.dataStore?.data?.first()?.get(BACKUP_DIRECTORY) ?: ""
}

suspend fun Context?.isAutoBackupEnabled(): Boolean {
    return this?.dataStore?.data?.first()?.get(AUTO_BACKUP) ?: false
}

suspend fun Context?.overrideAutoBackup(): Boolean {
    return this?.dataStore?.data?.first()?.get(OVERRIDE_AUTO_BACKUP) ?: false
}

suspend fun Context?.setOverrideAutoBackup(value: Boolean) {
    this?.dataStore?.edit {
        it[OVERRIDE_AUTO_BACKUP] = value
    }
}

suspend fun Context?.setAutoBackupEnabled(value: Boolean) {
    this?.dataStore?.edit {
        it[AUTO_BACKUP] = value
    }
}

suspend fun Context?.getBackupTime(): Long {
    return this?.dataStore?.data?.first()?.get(BACKUP_DATE_TIME) ?: -1
}

private val BACKUP_KEY = stringPreferencesKey("backup_key")
private val KEYPASS_PASSWORD = stringPreferencesKey("keypass_password")
private val BACKUP_DIRECTORY = stringPreferencesKey("backup_directory")
private val BACKUP_DATE_TIME = longPreferencesKey("backup_date_time")
private val AUTO_BACKUP = booleanPreferencesKey("auto_backup")
private val OVERRIDE_AUTO_BACKUP = booleanPreferencesKey("override_auto_backup")
