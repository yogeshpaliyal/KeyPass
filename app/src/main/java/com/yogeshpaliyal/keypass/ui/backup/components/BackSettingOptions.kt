package com.yogeshpaliyal.keypass.ui.backup.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.commonComponents.PreferenceItem
import com.yogeshpaliyal.keypass.ui.redux.states.BackupScreenState

@Composable
fun BackSettingOptions(
    state: BackupScreenState,
    updatedState: (BackupScreenState) -> Unit,
    launchDirectorySelector: () -> Unit
) {
    Column {
        PreferenceItem(summary = R.string.backup_desc)
        AnimatedVisibility(visible = state.isBackupEnabled == true) {
            BackupEnableOptions(state, updatedState, launchDirectorySelector)
        }

        AnimatedVisibility(visible = state.isBackupEnabled != true) {
            PreferenceItem(title = R.string.turn_on_backup, onClickItem = launchDirectorySelector)
        }
    }
}
