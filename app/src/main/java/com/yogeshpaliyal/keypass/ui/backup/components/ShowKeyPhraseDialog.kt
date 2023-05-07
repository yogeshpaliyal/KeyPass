package com.yogeshpaliyal.keypass.ui.backup.components

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.yogeshpaliyal.common.utils.backupAccounts
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.CopyToClipboard
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import com.yogeshpaliyal.keypass.ui.settings.MySettingsViewModel
import org.reduxkotlin.compose.rememberTypedDispatcher

@Composable
fun ShowKeyPhraseDialog(
    selectedDirectory: Uri?,
    mySettingsViewModel: MySettingsViewModel = hiltViewModel(),
    onYesClicked: () -> Unit,
    saveKeyphrase: () -> Unit
) {
    if (selectedDirectory == null) {
        return
    }

    val dispatchAction = rememberTypedDispatcher<Action>()

    val context = LocalContext.current
    val (backupInfo, setBackupInfo) = remember {
        mutableStateOf<Pair<Boolean, String>?>(null)
    }

    LaunchedEffect(key1 = selectedDirectory, block = {
        val localBackupInfo = context.backupAccounts(
            mySettingsViewModel.appDb,
            selectedDirectory
        )
        setBackupInfo(localBackupInfo)
        saveKeyphrase()
    })

    if (backupInfo != null) {
        val newKeyCreated = backupInfo.first
        if (newKeyCreated) {
            AlertDialog(onDismissRequest = {}, title = {
                Text(text = stringResource(id = R.string.alert))
            }, confirmButton = {
                    TextButton(onClick = {
                        onYesClicked()
                    }) {
                        Text(stringResource(id = R.string.yes))
                    }
                }, text = {
                    Column {
                        Text(text = stringResource(id = R.string.copy_keypharse_msg))
                        TextButton(onClick = {
                            dispatchAction(CopyToClipboard(backupInfo.second))
                        }) {
                            Text(text = backupInfo.second)
                        }
                    }
                })
        } else {
            dispatchAction(ToastAction(R.string.backup_completed))
        }
    }
}
