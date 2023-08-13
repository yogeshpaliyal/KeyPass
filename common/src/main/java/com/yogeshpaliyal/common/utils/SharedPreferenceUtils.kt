package com.yogeshpaliyal.common.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yogeshpaliyal.common.BuildConfig
import com.yogeshpaliyal.common.data.DEFAULT_PASSWORD_LENGTH
import com.yogeshpaliyal.common.data.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 21-02-2021 11:18
*/

val Context.dataStore by preferencesDataStore(
    name = "settings"
)

private var userSettingsDataStore: DataStore<UserSettings>? = null
private fun Context.getUserSettingsDataStore(): DataStore<UserSettings> {
    val res = userSettingsDataStore ?: UserSettingsDataStore(this).getDataStore()
    userSettingsDataStore = res
    return res
}

suspend fun Context.getUserSettings(): UserSettings {
    return getUserSettingsDataStore().data.firstOrNull() ?: UserSettings()
}

fun Context.getUserSettingsFlow(): Flow<UserSettings> {
    return getUserSettingsDataStore().data
}

suspend fun Context.getUserSettingsOrNull(): UserSettings? {
    return getUserSettingsDataStore().data.firstOrNull()
}

suspend fun Context.setKeyPassPassword(password: String?) {
    getUserSettingsDataStore().updateData {
        it.copy(keyPassPassword = password)
    }
}

suspend fun Context.setDefaultPasswordLength(password: Float) {
    getUserSettingsDataStore().updateData {
        it.copy(defaultPasswordLength = password)
    }
}

suspend fun Context.setBiometricEnable(isBiometricEnable: Boolean) {
    getUserSettingsDataStore().updateData {
        it.copy(isBiometricEnable = isBiometricEnable)
    }
}

suspend fun Context.setBackupDirectory(backupDirectory: String) {
    getUserSettingsDataStore().updateData {
        it.copy(backupDirectory = backupDirectory)
    }
}

suspend fun Context.setBackupKey(backupKey: String?) {
    getUserSettingsDataStore().updateData {
        it.copy(backupKey = backupKey)
    }
}

suspend fun Context.setDatabasePassword(databasePassword: String) {
    getUserSettingsDataStore().updateData {
        it.copy(dbPassword = databasePassword)
    }
}

suspend fun Context.setBackupTime(backupTime: Long?) {
    getUserSettingsDataStore().updateData {
        it.copy(backupTime = backupTime)
    }
}

suspend fun Context.setAutoBackupEnabled(autoBackupEnable: Boolean) {
    getUserSettingsDataStore().updateData {
        it.copy(autoBackupEnable = autoBackupEnable)
    }
}

suspend fun Context.setOverrideAutoBackup(overrideAutoBackup: Boolean) {
    getUserSettingsDataStore().updateData {
        it.copy(overrideAutoBackup = overrideAutoBackup)
    }
}

suspend fun Context.setUserSettings(userSettings: UserSettings) {
    getUserSettingsDataStore().updateData {
        userSettings
    }
}

suspend fun Context.clearBackupKey() {
    getUserSettingsDataStore().updateData {
        it.backupKey ?: return@updateData it
        it.copy(backupKey = null)
    }
}

const val BACKUP_KEY_LENGTH = 16

/**
 * Pair
 * 1st => true if key is created now & false if key is created previously
 *
 */
suspend fun Context.getOrCreateBackupKey(reset: Boolean = false): Pair<Boolean, String> {
    val userSettings = getUserSettings()
    return if (userSettings.backupKey != null && reset.not()) {
        Pair(false, userSettings.backupKey)
    } else {
        val randomKey = getRandomString(BACKUP_KEY_LENGTH)
        setBackupKey(randomKey)
        Pair(true, randomKey)
    }
}

private val BACKUP_KEY = stringPreferencesKey("backup_key")
private val BIOMETRIC_ENABLE = booleanPreferencesKey("biometric_enable")
private val KEYPASS_PASSWORD = stringPreferencesKey("keypass_password")
private val KEYPASS_PASSWORD_LENGTH = floatPreferencesKey("keypass_password_length")
private val BACKUP_DIRECTORY = stringPreferencesKey("backup_directory")
private val BACKUP_DATE_TIME = longPreferencesKey("backup_date_time")
private val AUTO_BACKUP = booleanPreferencesKey("auto_backup")
private val OVERRIDE_AUTO_BACKUP = booleanPreferencesKey("override_auto_backup")

suspend fun Context.migrateOldDataToNewerDataStore() {
    var userSettings = getUserSettingsOrNull() ?: return

    val olderData = this.dataStore.data.first()

    if (olderData.contains(BACKUP_KEY)) {
        userSettings = userSettings.copy(backupKey = olderData[BACKUP_KEY])
    }

    if (olderData.contains(BIOMETRIC_ENABLE)) {
        userSettings = userSettings.copy(isBiometricEnable = olderData[BIOMETRIC_ENABLE] ?: false)
    }

    if (olderData.contains(KEYPASS_PASSWORD)) {
        userSettings = userSettings.copy(keyPassPassword = olderData[KEYPASS_PASSWORD])
    }

    if (olderData.contains(KEYPASS_PASSWORD_LENGTH)) {
        userSettings = userSettings.copy(defaultPasswordLength = olderData[KEYPASS_PASSWORD_LENGTH] ?: DEFAULT_PASSWORD_LENGTH)
    }

    if (olderData.contains(BACKUP_DIRECTORY)) {
        userSettings = userSettings.copy(backupDirectory = olderData[BACKUP_DIRECTORY])
    }

    if (olderData.contains(BACKUP_DATE_TIME)) {
        userSettings = userSettings.copy(backupTime = olderData[BACKUP_DATE_TIME])
    }

    if (olderData.contains(AUTO_BACKUP)) {
        userSettings = userSettings.copy(autoBackupEnable = olderData[AUTO_BACKUP] ?: false)
    }

    if (olderData.contains(OVERRIDE_AUTO_BACKUP)) {
        userSettings = userSettings.copy(overrideAutoBackup = olderData[OVERRIDE_AUTO_BACKUP] ?: false)
    }

    clearDataStoreOld()
    setUserSettings(userSettings)
}

private suspend fun Context.clearDataStoreOld() {
    this.dataStore.edit {
        it.remove(BACKUP_KEY)
        it.remove(BIOMETRIC_ENABLE)
        it.remove(KEYPASS_PASSWORD)
        it.remove(KEYPASS_PASSWORD_LENGTH)
        it.remove(BACKUP_DIRECTORY)
        it.remove(BACKUP_DATE_TIME)
        it.remove(AUTO_BACKUP)
        it.remove(OVERRIDE_AUTO_BACKUP)
    }
}
