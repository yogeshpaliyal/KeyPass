package com.yogeshpaliyal.keypass.ui.redux.states

import com.yogeshpaliyal.keypass.importer.AccountsImporter

data class BackupImporterState(
    val selectedImported: AccountsImporter? = null
) : ScreenState()
