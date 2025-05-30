package com.yogeshpaliyal.keypass.ui.auth.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.yogeshpaliyal.common.utils.setKeyPassPassword
import com.yogeshpaliyal.common.utils.updateLastPasswordLoginTime
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.nav.LocalUserSettings
import com.yogeshpaliyal.keypass.ui.redux.KeyPassRedux
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.GoBackAction
import com.yogeshpaliyal.keypass.ui.redux.actions.NavigationAction
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import com.yogeshpaliyal.keypass.ui.redux.states.AuthState
import com.yogeshpaliyal.keypass.ui.redux.states.HomeState
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun ButtonBar(
    state: AuthState,
    password: String,
    setPasswordError: (Int?) -> Unit,
    dispatchAction: (Action) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val userSettings = LocalUserSettings.current
    val (biometricEnable, setBiometricEnable) = remember(state) { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth(1f), Arrangement.SpaceEvenly) {
        AnimatedVisibility(state is AuthState.ConfirmPassword) {
            Button(onClick = {
                dispatchAction(NavigationAction(AuthState.CreatePassword, true))
            }) {
                Text(text = stringResource(id = R.string.back))
            }
        }

        if (userSettings.isBiometricEnable && state is AuthState.Login) {
            OutlinedButton(onClick = {

                val currentTime = System.currentTimeMillis()
                val lastPasswordLoginTime = userSettings.lastPasswordLoginTime ?: -1
                if (userSettings.biometricLoginTimeoutEnable != true || (lastPasswordLoginTime > 0 && (currentTime - lastPasswordLoginTime).toDuration(
                        DurationUnit.MILLISECONDS
                    ).inWholeHours < 24)
                ) {
                    setBiometricEnable(true)
                } else {
                    // User exceeds 24 hours before entering the password
                    dispatchAction(ToastAction(R.string.biometric_disabled_due_to_timeout))
                }
            }) {
                Text(text = stringResource(id = R.string.unlock_with_biometric))
            }

            BiometricPrompt(show = biometricEnable) {
                setBiometricEnable(false)
            }
        }

        Button(onClick = {
            when (state) {
                is AuthState.CreatePassword -> {
                    if (password.isBlank()) {
                        setPasswordError(R.string.enter_password)
                    } else {
                        dispatchAction(NavigationAction(AuthState.ConfirmPassword(password)))
                    }
                }

                is AuthState.ConfirmPassword -> {
                    if (state.password == password) {
                        coroutineScope.launch {
                            context.setKeyPassPassword(password)
                            dispatchAction(NavigationAction(HomeState(), true))
                        }
                    } else {
                        setPasswordError(R.string.password_no_match)
                    }
                }

                is AuthState.Login -> {
                    coroutineScope.launch {
                        val savedPassword = userSettings.keyPassPassword
                        if (savedPassword == password) {
                            if(userSettings.biometricLoginTimeoutEnable == true) {
                                context.updateLastPasswordLoginTime(System.currentTimeMillis())
                            }
                            KeyPassRedux.getLastScreen()?.let {
                                dispatchAction(GoBackAction)
                            } ?: dispatchAction(NavigationAction(HomeState(), true))
                        } else {
                            setPasswordError(R.string.incorrect_password)
                        }
                    }
                }
            }
        }) {
            Text(text = stringResource(id = R.string.str_continue))
        }
    }
}
