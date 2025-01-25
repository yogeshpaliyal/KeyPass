package com.yogeshpaliyal.keypass.ui.home.components

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.dbhelper.restoreBackup
import com.yogeshpaliyal.common.utils.BACKUP_KEY_LENGTH
import com.yogeshpaliyal.common.utils.setBackupDirectory
import com.yogeshpaliyal.common.utils.updateLastKeyPhraseEnterTime
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.nav.LocalUserSettings
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import kotlinx.coroutines.launch
import org.reduxkotlin.compose.rememberTypedDispatcher

@Composable
fun ValidateKeyPhraseDialog(
    hideDialog: () -> Unit
) {
    val (keyphrase, setKeyPhrase) = remember {
        mutableStateOf("")
    }

    val dispatchAction = rememberTypedDispatcher<Action>()

    val userSettings = LocalUserSettings.current
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = {
            hideDialog()
        },
        title = {
            Text(text = stringResource(id = R.string.validate_keyphrase))
        },
        confirmButton = {
            TextButton(onClick = {
                if (keyphrase.isEmpty()) {
                    dispatchAction(ToastAction(R.string.alert_blank_keyphrase))
                    return@TextButton
                }

                if (userSettings.backupKey != keyphrase) {
                    dispatchAction(ToastAction(R.string.mismatch_keyphrase))
                    return@TextButton
                }

                coroutineScope.launch {
                    context.updateLastKeyPhraseEnterTime(System.currentTimeMillis())
                    hideDialog()
                    dispatchAction(ToastAction(R.string.valid_keyphrase))
                }
            }) {
                Text(text = stringResource(id = R.string.validate))
            }
        },
        dismissButton = {
            TextButton(onClick = hideDialog) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(1f)) {
                Text(text = stringResource(id = R.string.keyphrase_restore_info))
                Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(1f),
                    value = keyphrase,
                    onValueChange = setKeyPhrase,
                    placeholder = {
                        Text(text = stringResource(id = R.string.enter_keyphrase))
                    }
                )
            }
        }
    )
}
