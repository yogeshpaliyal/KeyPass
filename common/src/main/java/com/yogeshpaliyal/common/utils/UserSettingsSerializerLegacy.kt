package com.yogeshpaliyal.common.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.Serializer
import com.yogeshpaliyal.common.data.DEFAULT_PASSWORD_LENGTH
import com.yogeshpaliyal.common.data.UserSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class UserSettingsSerializerLegacy constructor(
    private val applicationContext: Context
) : Serializer<UserSettings> {

    override val defaultValue: UserSettings
        get() = UserSettings()

    override suspend fun readFrom(input: InputStream): UserSettings {
        return UserSettings(
            autoBackupEnable = applicationContext.isAutoBackupEnabledLegacy(),
            backupDirectory = applicationContext.getBackupDirectoryLegacy(),
            backupTime = applicationContext.getBackupTimeLegacy(),
            defaultPasswordLength = applicationContext.getKeyPassPasswordLengthLegacy() ?: DEFAULT_PASSWORD_LENGTH,
            isBiometricEnable = applicationContext.isBiometricEnableLegacy(),
            keyPassPassword = applicationContext.getKeyPassPasswordLegacy(),
            overrideAutoBackup = applicationContext.overrideAutoBackupLegacy(),
            backupKey = applicationContext.getOrCreateBackupKey(false).second
        )
    }

    override suspend fun writeTo(t: UserSettings, output: OutputStream) {
        applicationContext.setBiometricEnableLegacy(t.isBiometricEnable)
        applicationContext.setAutoBackupEnabledLegacy(t.autoBackupEnable)
        applicationContext.setBackupDirectoryLegacy(t.backupDirectory ?: "")
        applicationContext.setBackupTimeLegacy(t.backupTime ?: 0L)
        applicationContext.setKeyPassPasswordLengthLegacy(t.defaultPasswordLength)
        applicationContext.setKeyPassPasswordLegacy(t.keyPassPassword)
        applicationContext.setOverrideAutoBackupLegacy(t.overrideAutoBackup)
        applicationContext.saveKeyphraseLegacy(t.backupKey ?: "")
    }
}