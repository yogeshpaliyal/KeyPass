package com.yogeshpaliyal.keypass.importer

import android.net.Uri
import androidx.compose.runtime.Composable
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.ui.redux.actions.Action

interface AccountsImporter {
    fun getImporterTitle(): Int

    fun getImporterDesc(): Int?

    fun allowedMimeType(): String

    @Composable
    fun readFile(file: Uri, resolve: (List<AccountModel>) -> Unit, onCompleteOrCancel: (action: Action?) -> Unit)
}
