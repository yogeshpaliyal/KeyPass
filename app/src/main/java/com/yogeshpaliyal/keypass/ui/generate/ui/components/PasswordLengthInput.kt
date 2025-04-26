package com.yogeshpaliyal.keypass.ui.generate.ui.components

import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

const val DEFAULT_PASSWORD_LENGTH = 10f

@Composable
fun PasswordLengthInput(
    length: Float,
    onPasswordLengthChange: (Float) -> Unit
) {
    Text(text = "Password Length: ${length.toInt()}")

    Slider(
        value = length,
        onValueChange = onPasswordLengthChange,
        valueRange = 4f..256f,
        steps = 252
    )
}
