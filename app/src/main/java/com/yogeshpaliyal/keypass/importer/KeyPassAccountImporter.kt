package com.yogeshpaliyal.keypass.importer

import android.net.Uri
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.UpdateDialogAction

class KeyPassAccountImporter : AccountsImporter {
    override fun getImporterTitle(): Int {
        return R.string.keypass_backup
    }

    override fun getImporterDesc(): Int? = null

    override fun allowedMimeType(): String {
        return "*/*"
    }

    override fun readFileGetAction(
        file: Uri
    ): Action {
        return UpdateDialogAction(com.yogeshpaliyal.keypass.ui.redux.states.RestoreKeyPassBackupState(file))
    }
}
