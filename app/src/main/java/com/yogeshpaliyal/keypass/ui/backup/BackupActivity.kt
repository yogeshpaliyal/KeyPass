package com.yogeshpaliyal.keypass.ui.backup

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.yogeshpaliyal.common.utils.canUserAccessBackupDirectory
import com.yogeshpaliyal.common.utils.clearBackupKey
import com.yogeshpaliyal.common.utils.formatCalendar
import com.yogeshpaliyal.common.utils.getBackupDirectory
import com.yogeshpaliyal.common.utils.getBackupTime
import com.yogeshpaliyal.common.utils.isAutoBackupEnabled
import com.yogeshpaliyal.common.utils.isKeyPresent
import com.yogeshpaliyal.common.utils.overrideAutoBackup
import com.yogeshpaliyal.common.utils.setAutoBackupEnabled
import com.yogeshpaliyal.common.utils.setBackupDirectory
import com.yogeshpaliyal.common.utils.setOverrideAutoBackup
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.backup.components.BackupDialogs
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.StateUpdateAction
import com.yogeshpaliyal.keypass.ui.redux.states.BackupScreenState
import com.yogeshpaliyal.keypass.ui.redux.states.SelectKeyphraseType
import com.yogeshpaliyal.keypass.ui.redux.states.ShowKeyphrase
import com.yogeshpaliyal.keypass.ui.settings.PreferenceItem
import kotlinx.coroutines.launch
import org.reduxkotlin.compose.rememberTypedDispatcher


@Composable
fun BackupScreen(state: BackupScreenState) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val dispatchAction = rememberTypedDispatcher<Action>()

    val launcher = rememberLauncherForActivityResult(KeyPassBackupDirectoryPick()) {

        if (it == null) {
            return@rememberLauncherForActivityResult
        }

        context.contentResolver.takePersistableUriPermission(
            it,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )


        coroutineScope.launch {
            val dialogType = if (context.isKeyPresent()) ShowKeyphrase else SelectKeyphraseType
            dispatchAction(
                StateUpdateAction(
                    state.copy(
                        backupDirectory = it,
                        dialog = dialogType
                    )
                )
            )
        }
    }

    LaunchedEffect(key1 = state, block = {
        state.backupDirectory?.let {
            context.setBackupDirectory(it.toString())
        }
        state.isAutoBackupEnabled?.let {
            context.setAutoBackupEnabled(it)
        }
        state.overrideAutoBackup?.let {
            context.setOverrideAutoBackup(it)
        }
        state.isBackupEnabled?.let {
            if (it.not()) {
                context.clearBackupKey()
            }
        }
    })

    LaunchedEffect(key1 = context, block = {
        val isBackupEnabled = (
                context.canUserAccessBackupDirectory() && (context.isKeyPresent()))

        val isAutoBackupEnabled = context.isAutoBackupEnabled()
        val overrideAutoBackup = context.overrideAutoBackup()

        val lastBackupTime = context.getBackupTime()
        val backupDirectory = context.getBackupDirectory()


        dispatchAction(
            StateUpdateAction(
                state.copy(
                    isBackupEnabled = isBackupEnabled,
                    isAutoBackupEnabled = isAutoBackupEnabled,
                    overrideAutoBackup = overrideAutoBackup,
                    lastBackupTime = lastBackupTime,
                    backupDirectory = Uri.parse(backupDirectory)
                )
            )
        )
    })

    BackSettingOptions(state, updatedState = {
        dispatchAction(StateUpdateAction(it))
    }) {
        launcher.launch(arrayOf())
    }

    BackupDialogs(state = state) {
        dispatchAction(StateUpdateAction(it))
    }

}


@Composable
fun BackSettingOptions(
    state: BackupScreenState,
    updatedState: (BackupScreenState) -> Unit,
    launchDirectorySelector: () -> Unit
) {
    Column {
        PreferenceItem(summary = R.string.backup_desc)
        if (state.isBackupEnabled == true) {
            PreferenceItem(
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
            PreferenceItem(title = R.string.turn_off_backup) {

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
        } else {

            PreferenceItem(title = R.string.turn_on_backup, onClickItem = launchDirectorySelector)
        }

    }
}


@Composable
fun AutoBackup(
    isAutoBackupEnabled: Boolean?,
    overrideAutoBackup: Boolean?,
    setAutoBackupEnabled: (Boolean) -> Unit,
    setOverrideAutoBackup: (Boolean) -> Unit
) {
    PreferenceItem(
        title = R.string.auto_backup,
        summary = if (isAutoBackupEnabled == true) R.string.enabled else R.string.disabled
    ) {
        setAutoBackupEnabled(!(isAutoBackupEnabled ?: false))
    }
    if (isAutoBackupEnabled == true) {
        PreferenceItem(title = R.string.auto_backup, isCategory = true)
        PreferenceItem(summary = R.string.auto_backup_desc)
        PreferenceItem(
            title = R.string.override_auto_backup_file,
            summary = if (overrideAutoBackup == true) R.string.enabled else R.string.disabled,
        ) {
            setOverrideAutoBackup(!(overrideAutoBackup ?: false))
        }
    }
}