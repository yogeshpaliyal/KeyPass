package com.yogeshpaliyal.keypass.ui.commonComponents

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun KeyPassInputField(
    modifier: Modifier = Modifier,
    @StringRes placeholder: Int,
    value: String?,
    setValue: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    copyToClipboardClicked: ((String) -> Unit)? = null
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value ?: "",
        label = {
            Text(text = stringResource(id = placeholder))
        },
        onValueChange = setValue,
        leadingIcon = leadingIcon,
        trailingIcon = {
            Row {
                trailingIcon?.invoke()

                if (copyToClipboardClicked != null) {
                    AnimatedVisibility(value.isNullOrBlank().not()) {
                        IconButton(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            onClick = {
                                if (value != null) {
                                    copyToClipboardClicked(value)
                                }
                            }
                        ) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Rounded.ContentCopy),
                                contentDescription = "Copy To Clipboard"
                            )
                        }
                    }
                }
            }
        },
        visualTransformation = visualTransformation
    )
}
