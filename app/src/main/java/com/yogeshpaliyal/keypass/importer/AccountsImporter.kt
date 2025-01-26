package com.yogeshpaliyal.keypass.importer

import android.net.Uri
import com.yogeshpaliyal.keypass.ui.redux.actions.Action

interface AccountsImporter {
    fun getImporterTitle(): Int

    fun getImporterDesc(): Int?

    fun allowedMimeType(): String

    fun readFileGetAction(file: Uri): Action
}
