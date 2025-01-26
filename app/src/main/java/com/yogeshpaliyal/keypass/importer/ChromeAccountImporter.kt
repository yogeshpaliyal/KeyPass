package com.yogeshpaliyal.keypass.importer

import android.net.Uri
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.UpdateDialogAction
import com.yogeshpaliyal.keypass.ui.redux.states.RestoreChromeBackupState

class ChromeAccountImporter : AccountsImporter {
    override fun getImporterTitle(): Int = R.string.google_backup
    override fun getImporterDesc(): Int? = null

    override fun allowedMimeType(): String {
        return "text/comma-separated-values"
    }

    override fun readFileGetAction(
        file: Uri
    ): Action {
        return UpdateDialogAction(RestoreChromeBackupState(file))
    }
}
