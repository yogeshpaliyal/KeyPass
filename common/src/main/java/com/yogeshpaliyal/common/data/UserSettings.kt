package com.yogeshpaliyal.common.data

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

const val DEFAULT_PASSWORD_LENGTH = 10f

@Keep
@Serializable
data class UserSettings(
    val keyPassPassword: String? = null,
    val dbPassword: String? = null,
    val defaultPasswordLength: Float = DEFAULT_PASSWORD_LENGTH,
    val backupKey: String? = null,
    val isBiometricEnable: Boolean = false,
    val backupDirectory: String? = null,
    val backupTime: Long? = null,
    val autoBackupEnable: Boolean = false,
    val overrideAutoBackup: Boolean = false,
    val lastAppVersion: Int? = null,
    val currentAppVersion: Int? = null
) {
    fun isKeyPresent() = backupKey != null
}
