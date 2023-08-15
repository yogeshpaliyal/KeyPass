package com.yogeshpaliyal.keypass.ui.backup

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.yogeshpaliyal.common.utils.canUserAccessBackupDirectory
import com.yogeshpaliyal.common.utils.clearBackupKey
import com.yogeshpaliyal.common.utils.setAutoBackupEnabled
import com.yogeshpaliyal.common.utils.setBackupDirectory
import com.yogeshpaliyal.common.utils.setOverrideAutoBackup
import com.yogeshpaliyal.keypass.ui.backup.components.BackSettingOptions
import com.yogeshpaliyal.keypass.ui.backup.components.BackupDialogs
import com.yogeshpaliyal.keypass.ui.commonComponents.DefaultBottomAppBar
import com.yogeshpaliyal.keypass.ui.nav.LocalUserSettings
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.StateUpdateAction
import com.yogeshpaliyal.keypass.ui.redux.states.BackupScreenState
import com.yogeshpaliyal.keypass.ui.redux.states.SelectKeyphraseType
import com.yogeshpaliyal.keypass.ui.redux.states.ShowKeyphrase
import kotlinx.coroutines.launch
import org.reduxkotlin.compose.rememberTypedDispatcher

@Composable
fun BackupScreen(state: BackupScreenState) {
    val context = LocalContext.current
    val userSettings = LocalUserSettings.current
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
            val dialog = if (userSettings.isKeyPresent()) {
                ShowKeyphrase
            } else {
                SelectKeyphraseType
            }
            dispatchAction(
                StateUpdateAction(
                    state.copy(
                        backupDirectory = it,
                        dialog = dialog
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
    })

    LaunchedEffect(key1 = state.isBackupEnabled, block = {
        state.isBackupEnabled?.let {
            if (it.not()) {
                context.clearBackupKey()
            }
        }
    })

    LaunchedEffect(key1 = Unit, block = {
        val isBackupEnabled = (
            context.canUserAccessBackupDirectory() && (userSettings.isKeyPresent())
            )

        val isAutoBackupEnabled = userSettings.autoBackupEnable
        val overrideAutoBackup = userSettings.overrideAutoBackup

        val lastBackupTime = userSettings.backupTime
        val backupDirectory =
            if (userSettings.backupDirectory != null) Uri.parse(userSettings.backupDirectory) else null

        dispatchAction(
            StateUpdateAction(
                state.copy(
                    isBackupEnabled = isBackupEnabled,
                    isAutoBackupEnabled = isAutoBackupEnabled,
                    overrideAutoBackup = overrideAutoBackup,
                    lastBackupTime = lastBackupTime,
                    backupDirectory = backupDirectory
                )
            )
        )
    })

    Scaffold(bottomBar = {
        DefaultBottomAppBar()
    }) { contentPadding ->
        Surface(modifier = Modifier.padding(contentPadding)) {
            BackSettingOptions(state, updatedState = {
                dispatchAction(StateUpdateAction(it))
            }) {
                launcher.launch(arrayOf())
            }
        }
    }

    BackupDialogs(state = state) {
        dispatchAction(StateUpdateAction(it))
    }
}
