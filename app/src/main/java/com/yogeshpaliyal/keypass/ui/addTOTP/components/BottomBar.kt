package com.yogeshpaliyal.keypass.ui.addTOTP.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import com.yogeshpaliyal.keypass.ui.detail.components.DeleteConfirmation

@Composable
fun BottomBar(
    backPressed: () -> Unit,
    showDeleteButton: Boolean,
    onDeletePressed: () -> Unit,
    onSaveClicked: () -> Unit
) {
    val (openDialog, setOpenDialog) = remember { mutableStateOf(false) }

    BottomAppBar(actions = {
        IconButton(onClick = backPressed) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Rounded.ArrowBackIosNew),
                contentDescription = "Go Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        if (showDeleteButton) {
            IconButton(onClick = {
                setOpenDialog(true)
            }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.Delete),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }, floatingActionButton = {
            FloatingActionButton(modifier = Modifier.testTag("save"), onClick = onSaveClicked) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.Done),
                    contentDescription = "Save Changes"
                )
            }
        })

    DeleteConfirmation(
        openDialog,
        updateDialogVisibility = {
            setOpenDialog(it)
        },
        onDeletePressed
    )
}
