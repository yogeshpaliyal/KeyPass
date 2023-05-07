package com.yogeshpaliyal.keypass.ui.backup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yogeshpaliyal.keypass.R

@Composable
fun SelectKeyphraseType(
    customKeyphrase: () -> Unit,
    generateKeyphrase: () -> Unit,
    dismissDialog: () -> Unit
) {
    AlertDialog(onDismissRequest = dismissDialog, title = {
        Text(text = stringResource(id = R.string.alert))
    }, confirmButton = {
            TextButton(onClick = customKeyphrase) {
                Text(stringResource(id = R.string.custom_keyphrase))
            }
        }, dismissButton = {
            TextButton(onClick = generateKeyphrase) {
                Text(stringResource(id = R.string.generate_keyphrase))
            }
        }, text = {
            Column {
                Text(text = stringResource(id = R.string.custom_generated_keyphrase_info))
            }
        })
}
