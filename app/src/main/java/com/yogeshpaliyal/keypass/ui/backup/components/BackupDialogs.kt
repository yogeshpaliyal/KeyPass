package com.yogeshpaliyal.keypass.ui.backup.components

import androidx.compose.runtime.Composable
import com.yogeshpaliyal.keypass.ui.redux.states.BackupScreenState
import com.yogeshpaliyal.keypass.ui.redux.states.CustomKeyphrase
import com.yogeshpaliyal.keypass.ui.redux.states.SelectKeyphraseType
import com.yogeshpaliyal.keypass.ui.redux.states.ShowKeyphrase

@Composable
fun BackupDialogs(state: BackupScreenState, updateState: (BackupScreenState) -> Unit) {
    when (state.dialog) {
        is SelectKeyphraseType -> {
            SelectKeyphraseType(customKeyphrase = {
                updateState(state.copy(dialog = CustomKeyphrase))
            }, generateKeyphrase = {
                updateState(state.copy(dialog = ShowKeyphrase))
            }) {
                updateState(state.copy(dialog = null))
            }
        }

        is CustomKeyphrase -> {
            CreateCustomKeyphrase(saveKeyphrase = {
                updateState(state.copy(dialog = ShowKeyphrase))
            }) {
                updateState(state.copy(dialog = null))
            }
        }

        is ShowKeyphrase -> {
            ShowKeyPhraseDialog(selectedDirectory = state.backupDirectory, onYesClicked = {
                updateState(state.copy(dialog = null, isBackupEnabled = true))
            }) {
                updateState(state.copy(isBackupEnabled = true))
            }
        }

        null -> {
            // Do Noting Do not show dialog
        }
    }
}
