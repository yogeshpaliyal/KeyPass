package com.yogeshpaliyal.keypass.ui.changePassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.common.utils.getKeyPassPassword
import com.yogeshpaliyal.common.utils.setKeyPassPassword
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.auth.components.PasswordInputField
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.GoBackAction
import com.yogeshpaliyal.keypass.ui.redux.actions.StateUpdateAction
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import com.yogeshpaliyal.keypass.ui.redux.states.ChangeAppPasswordState
import kotlinx.coroutines.launch
import org.reduxkotlin.TypedDispatcher
import org.reduxkotlin.compose.rememberTypedDispatcher

fun TypedDispatcher<Action>.updateState(state: ChangeAppPasswordState) {
    this(StateUpdateAction(state = state))
}

@Composable
fun ChangePassword(state: ChangeAppPasswordState) {
    val dispatchAction = rememberTypedDispatcher<Action>()

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(bottomBar = {
        BottomAppBar {
            IconButton(onClick = {
                dispatchAction(GoBackAction)
            }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.ArrowBackIosNew),
                    contentDescription = "Go Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }) { contentPadding ->
        Surface(modifier = Modifier.padding(contentPadding)) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxSize(1f)
                    .verticalScroll(rememberScrollState()),
                Arrangement.SpaceEvenly,
                Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_undraw_unlock_24mb),
                    contentDescription = ""
                )

                Text(text = stringResource(id = R.string.change_app_password))

                PasswordInputField(
                    R.string.old_password,
                    state.oldPassword.password,
                    {
                        dispatchAction.updateState(
                            state.copy(
                                oldPassword = state.oldPassword.copy(
                                    password = it,
                                    passwordError = null
                                )
                            )
                        )
                    },
                    state.oldPassword.passwordVisible,
                    {
                        dispatchAction.updateState(
                            state.copy(
                                oldPassword = state.oldPassword.copy(
                                    passwordVisible = it
                                )
                            )
                        )
                    },
                    state.oldPassword.passwordError
                )

                PasswordInputField(
                    R.string.new_password,
                    state.newPassword.password,
                    {
                        dispatchAction.updateState(
                            state.copy(
                                newPassword = state.newPassword.copy(
                                    password = it,
                                    passwordError = null
                                )
                            )
                        )
                    },
                    state.newPassword.passwordVisible,
                    {
                        dispatchAction.updateState(
                            state.copy(
                                newPassword = state.newPassword.copy(
                                    passwordVisible = it
                                )
                            )
                        )
                    },
                    state.newPassword.passwordError
                )

                PasswordInputField(
                    R.string.confirm_password,
                    state.confirmPassword.password,
                    {
                        dispatchAction.updateState(
                            state.copy(
                                confirmPassword = state.confirmPassword.copy(
                                    password = it,
                                    passwordError = null
                                )
                            )
                        )
                    },
                    state.confirmPassword.passwordVisible,
                    {
                        dispatchAction.updateState(
                            state.copy(
                                confirmPassword = state.confirmPassword.copy(
                                    passwordVisible = it
                                )
                            )
                        )
                    },
                    state.confirmPassword.passwordError
                )

                Row(modifier = Modifier.fillMaxWidth(1f), Arrangement.SpaceEvenly) {
                    Button(onClick = {
                        // Validate password and change
                        val oldPassword = state.oldPassword
                        if (oldPassword.password.isBlank()) {
                            dispatchAction.updateState(
                                state.copy(
                                    oldPassword = oldPassword.copy(
                                        passwordError = R.string.blank_old_password
                                    )
                                )
                            )
                            return@Button
                        }

                        val newPassword = state.newPassword
                        if (newPassword.password.isBlank()) {
                            dispatchAction.updateState(
                                state.copy(
                                    newPassword = newPassword.copy(
                                        passwordError = R.string.blank_new_password
                                    )
                                )
                            )
                            return@Button
                        }

                        val confirmPassword = state.confirmPassword
                        if (confirmPassword.password.isBlank()) {
                            dispatchAction.updateState(
                                state.copy(
                                    confirmPassword = confirmPassword.copy(
                                        passwordError = R.string.blank_confirm_password
                                    )
                                )
                            )
                            return@Button
                        }

                        coroutineScope.launch {
                            val oldPasswordFromStore = context.getKeyPassPassword()
                            if (oldPassword.password != oldPasswordFromStore) {
                                dispatchAction.updateState(
                                    state.copy(
                                        oldPassword = oldPassword.copy(
                                            passwordError = R.string.incorrect_old_password
                                        )
                                    )
                                )
                                return@launch
                            }

                            if (newPassword.password != confirmPassword.password) {
                                dispatchAction.updateState(
                                    state.copy(
                                        confirmPassword = confirmPassword.copy(
                                            passwordError = R.string.password_no_match
                                        )
                                    )
                                )
                                return@launch
                            }

                            context.setKeyPassPassword(newPassword.password)
                            dispatchAction(ToastAction(R.string.password_change_success))
                        }
                    }) {
                        Text(text = stringResource(id = R.string.change_app_password))
                    }
                }
            }
        }
    }
}
