package com.yogeshpaliyal.keypass.importer

import android.net.Uri
import androidx.compose.runtime.Composable
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.settings.RestoreDialog

class KeyPassAccountImporter : AccountsImporter {
    override fun getImporterTitle(): Int {
        return R.string.keypass_backup
    }

    override fun getImporterDesc(): Int? = null

    override fun allowedMimeType(): String {
        return "*/*"
    }

    @Composable
    override fun readFile(file: Uri, resolve: (List<AccountModel>) -> Unit, onCompleteOrCancel: (action: Action?) -> Unit) {
        RestoreDialog(
            selectedFile = file,
            hideDialog = {
                onCompleteOrCancel(null)
            },
            saveAccounts = resolve
        )
    }
}
