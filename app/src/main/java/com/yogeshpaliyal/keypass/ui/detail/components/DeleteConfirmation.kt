package com.yogeshpaliyal.keypass.ui.detail.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.yogeshpaliyal.keypass.R

@Composable
fun DeleteConfirmation(
    openDialog: Boolean,
    updateDialogVisibility: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = { /*TODO*/ },
            title = {
                Text(text = stringResource(id = R.string.delete_account_title))
            },
            confirmButton = {
                TextButton(
                    modifier = Modifier.testTag("delete"),
                    onClick = {
                        updateDialogVisibility(false)
                        onDelete()
                    }
                ) {
                    Text(text = stringResource(id = R.string.delete))
                }
            },
            text = {
                Text(text = stringResource(id = R.string.delete_account_msg))
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        updateDialogVisibility(false)
                    }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}
