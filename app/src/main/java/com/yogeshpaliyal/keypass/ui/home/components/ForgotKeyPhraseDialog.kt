package com.yogeshpaliyal.keypass.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.UpdateDialogState
import com.yogeshpaliyal.keypass.ui.redux.states.ValidateKeyPhrase
import org.reduxkotlin.compose.rememberTypedDispatcher

@Composable
fun ForgotKeyPhraseDialog() {
    val dispatchAction = rememberTypedDispatcher<Action>()

    val hideDialog: () -> Unit = {
        dispatchAction(UpdateDialogState(ValidateKeyPhrase))
    }

    AlertDialog(
        onDismissRequest = {
            hideDialog()
        },
        title = {
            Text(text = stringResource(id = R.string.forgot_keyphrase))
        },
        confirmButton = {
            TextButton(onClick = {
                hideDialog()
            }) {
                Text(text = stringResource(id = R.string.got_it))
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(1f)) {
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = stringResource(id = R.string.forgot_keyphrase_info))
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    )
}
