package com.yogeshpaliyal.keypass.ui.redux.states

import android.net.Uri
import java.net.URLDecoder

data class BackupScreenState(
    val isBackupEnabled: Boolean? = null,
    val isAutoBackupEnabled: Boolean? = null,
    val overrideAutoBackup: Boolean? = null,
    val lastBackupTime: Long? = null,
    val backupDirectory: Uri? = null,
    val dialog: BackupDialog? = null
) : ScreenState() {
    fun getFormattedBackupDirectory(): String {
        if (backupDirectory == null) {
            return ""
        }
        val splittedPath = URLDecoder.decode(backupDirectory.toString(), "utf-8").split("/")
        return splittedPath.get(splittedPath.lastIndex)
    }
}

sealed interface BackupDialog

object SelectKeyphraseType : BackupDialog

object CustomKeyphrase : BackupDialog

object ShowKeyphrase : BackupDialog
