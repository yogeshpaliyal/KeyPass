package com.yogeshpaliyal.keypass.ui.backupsImport

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.yogeshpaliyal.common.importer.KeyPassAccountImporter
import com.yogeshpaliyal.keypass.ui.redux.states.BackupImporterState
import com.yogeshpaliyal.keypass.ui.redux.states.KeyPassBackup
import com.yogeshpaliyal.keypass.ui.settings.OpenKeyPassBackup
import com.yogeshpaliyal.keypass.ui.settings.RestoreDialog

@Composable
fun BackupImporter(state: BackupImporterState) {

    val (result, setResult) = remember { mutableStateOf<Uri?>(null) }

    val launcher =
            rememberLauncherForActivityResult(OpenKeyPassBackup(state.selectedBackupType?.type)) {
                setResult(it)
            }


    result?.let {
        when(state.selectedBackupType) {
            is KeyPassBackup -> {
                RestoreDialog(
                    selectedFile = it,
                    hideDialog = {
                        setResult(null)
                    }
                )
            }
            else -> {

            }
        }
    }

}