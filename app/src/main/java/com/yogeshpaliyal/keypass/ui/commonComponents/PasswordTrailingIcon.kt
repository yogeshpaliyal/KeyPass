package com.yogeshpaliyal.keypass.ui.commonComponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter

@Composable
fun PasswordTrailingIcon(
    passwordVisible: Boolean,
    changePasswordVisibility: (updatedValue: Boolean) -> Unit
) {
    val description = if (passwordVisible) "Hide password" else "Show password"

    val image = if (passwordVisible) {
        Icons.Rounded.Visibility
    } else {
        Icons.Rounded.VisibilityOff
    }

    IconButton(onClick = { changePasswordVisibility(!passwordVisible) }) {
        Icon(
            painter = rememberVectorPainter(image = image),
            contentDescription = description
        )
    }
}
