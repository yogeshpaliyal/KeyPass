package com.yogeshpaliyal.keypass.ui.backup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yogeshpaliyal.common.utils.formatCalendar
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.commonComponents.PreferenceItem
import com.yogeshpaliyal.keypass.ui.redux.states.BackupScreenState
import com.yogeshpaliyal.keypass.ui.redux.states.ShowKeyphrase

@Composable
fun BackupEnableOptions(
    state: BackupScreenState,
    updatedState: (BackupScreenState) -> Unit,
    launchDirectorySelector: () -> Unit
) {
    Column {
        PreferenceItem(
            removeIconSpace = true,
            title = R.string.create_backup,
            summaryStr = stringResource(
                id = R.string.last_backup_date,
                state.lastBackupTime?.formatCalendar("dd MMM yyyy hh:mm aa") ?: ""
            )
        ) {
            updatedState(state.copy(dialog = ShowKeyphrase))
        }
        PreferenceItem(
            title = R.string.backup_folder,
            removeIconSpace = true,
            summaryStr = state.getFormattedBackupDirectory(),
            onClickItem = launchDirectorySelector
        )

        AutoBackup(state.isAutoBackupEnabled, state.overrideAutoBackup, {
            updatedState(state.copy(isAutoBackupEnabled = it))
        }) {
            updatedState(state.copy(overrideAutoBackup = it))
        }

//            PreferenceItem(
//                title = R.string.verify_keyphrase,
//                summary = R.string.verify_keyphrase_message
//            )
        PreferenceItem(title = R.string.turn_off_backup, removeIconSpace = true,) {
            updatedState(
                state.copy(
                    isBackupEnabled = false,
                    isAutoBackupEnabled = false,
                    overrideAutoBackup = false,
                    lastBackupTime = -1,
                    backupDirectory = null
                )
            )
        }
    }
}
