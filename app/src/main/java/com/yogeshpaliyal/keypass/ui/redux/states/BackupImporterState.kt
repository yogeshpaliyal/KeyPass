package com.yogeshpaliyal.keypass.ui.redux.states

import com.yogeshpaliyal.common.importer.AccountsImporter
import com.yogeshpaliyal.common.importer.ChromeAccountImporter
import com.yogeshpaliyal.common.importer.KeyPassAccountImporter

data class BackupImporterState(
   val selectedBackupType: BackupType<*>? = null
) : ScreenState()

sealed class BackupType<T:AccountsImporter>(val type: T)

object KeyPassBackup : BackupType<KeyPassAccountImporter>(KeyPassAccountImporter())

object GoogleChromeBackup : BackupType<ChromeAccountImporter>(ChromeAccountImporter())
