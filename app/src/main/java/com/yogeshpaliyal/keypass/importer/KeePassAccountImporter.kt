package com.yogeshpaliyal.keypass.importer

import android.net.Uri
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.UpdateDialogAction
import com.yogeshpaliyal.keypass.ui.redux.states.RestoreKeePassBackupState

class KeePassAccountImporter : AccountsImporter {
    override fun getImporterTitle(): Int = R.string.keepass_backup
    override fun getImporterDesc(): Int = R.string.keepass_backup_desc

    override fun allowedMimeType(): String {
        return "text/comma-separated-values"
    }

    override fun readFileGetAction(
        file: Uri
    ): Action {
        return UpdateDialogAction(RestoreKeePassBackupState(file))
    }
}
