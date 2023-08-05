package com.yogeshpaliyal.keypass.ui.redux.states

import com.yogeshpaliyal.keypass.importer.AccountsImporter
import com.yogeshpaliyal.keypass.importer.ChromeAccountImporter
import com.yogeshpaliyal.keypass.importer.KeyPassAccountImporter

data class BackupImporterState(
   val selectedImported: AccountsImporter? = null
) : ScreenState()