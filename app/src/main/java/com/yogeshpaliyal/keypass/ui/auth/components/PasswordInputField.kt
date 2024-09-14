package com.yogeshpaliyal.keypass.ui.auth.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastActionStr
import org.reduxkotlin.compose.rememberTypedDispatcher

@Composable
fun PasswordInputField(
    label: Int = R.string.enter_password,
    password: String,
    setPassword: (String) -> Unit,
    passwordVisible: Boolean,
    setPasswordVisible: (Boolean) -> Unit,
    passwordError: Int?,
    hint: String? = null
) {
    val context = LocalContext.current
    val dispatchAction = rememberTypedDispatcher<Action>()

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(1f),
        value = password,
        singleLine = true,
        placeholder = {
            Text(text = stringResource(id = label))
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        onValueChange = setPassword,
        isError = passwordError != null,
        supportingText = {
            if (passwordError != null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = passwordError),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        trailingIcon = {
            val image = if (passwordVisible) {
                Icons.Rounded.Visibility
            } else Icons.Rounded.VisibilityOff

            // Please provide localized description for accessibility services
            val description = if (passwordVisible) "Hide password" else "Show password"
            Row {
                IconButton(onClick = { setPasswordVisible(!passwordVisible) }) {
                    Icon(imageVector = image, description)
                }
                hint?.let {
                    IconButton(onClick = {
                        dispatchAction(ToastActionStr(hint))
                    }) {
                        Icon(imageVector = Icons.Outlined.Info, "Hint")
                    }
                }
            }
        }
    )
}
