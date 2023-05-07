package com.yogeshpaliyal.keypass.ui.backup.components

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
import com.yogeshpaliyal.common.utils.BACKUP_KEY_LENGTH
import com.yogeshpaliyal.common.utils.saveKeyphrase
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import kotlinx.coroutines.launch
import org.reduxkotlin.compose.rememberTypedDispatcher

@Composable
fun CreateCustomKeyphrase(saveKeyphrase: () -> Unit, dismissDialog: () -> Unit) {
    val (keyphrase, setKeyPhrase) = remember {
        mutableStateOf("")
    }

    val dispatchAction = rememberTypedDispatcher<Action>()

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(onDismissRequest = dismissDialog, title = {
        Text(text = stringResource(id = R.string.set_keyphrase))
    }, confirmButton = {
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
                    context.saveKeyphrase(keyphrase)
                }

                saveKeyphrase.invoke()
            }) {
                Text(stringResource(id = R.string.yes))
            }
        }, text = {
            Column {
                Text(text = stringResource(id = R.string.set_keyphrase_info))
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
        })
}
