package com.yogeshpaliyal.keypass.importer

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
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import kotlinx.coroutines.launch
import org.reduxkotlin.compose.rememberTypedDispatcher

class KeyPassAccountImporter : AccountsImporter {
    override fun getImporterTitle(): Int {
        return R.string.keypass_backup
    }

    override fun getImporterDesc(): Int? = null

    override fun allowedMimeType(): String {
        return "*/*"
    }

    @Composable
    override fun readFile(file: Uri, resolve: (List<AccountModel>) -> Unit, onCompleteOrCancel: (action: Action?) -> Unit) {
        RestoreDialog(
            selectedFile = file,
            hideDialog = {
                onCompleteOrCancel(null)
            },
            saveAccounts = resolve
        )
    }
}

@Composable
fun RestoreDialog(
    selectedFile: Uri,
    hideDialog: () -> Unit,
    saveAccounts: (list: List<AccountModel>) -> Unit
) {
    val (keyphrase, setKeyPhrase) = remember {
        mutableStateOf("")
    }

    val dispatchAction = rememberTypedDispatcher<Action>()

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = {
            hideDialog()
        },
        title = {
            Text(text = stringResource(id = R.string.restore))
        },
        confirmButton = {
            TextButton(onClick = {
                if (keyphrase.isEmpty()) {
                    dispatchAction(ToastAction(R.string.alert_blank_keyphrase))
                    return@TextButton
                }

                if (keyphrase.length != BACKUP_KEY_LENGTH) {
                    dispatchAction(ToastAction(R.string.alert_invalid_keyphrase))
                    return@TextButton
                }
                coroutineScope.launch {
                    val result =
                        restoreBackup(keyphrase, context.contentResolver, selectedFile)

                    if (result != null) {
                        saveAccounts(result)
                        hideDialog()
                        dispatchAction(ToastAction(R.string.backup_restored))
                    } else {
                        dispatchAction(ToastAction(R.string.invalid_keyphrase))
                    }
                }
            }) {
                Text(text = stringResource(id = R.string.restore))
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
