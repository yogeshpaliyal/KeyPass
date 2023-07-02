package com.yogeshpaliyal.keypass.ui.auth.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.yogeshpaliyal.common.utils.setKeyPassPassword
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.nav.LocalUserSettings
import com.yogeshpaliyal.keypass.ui.redux.actions.NavigationAction
import com.yogeshpaliyal.keypass.ui.redux.states.AuthState
import com.yogeshpaliyal.keypass.ui.redux.states.HomeState
import kotlinx.coroutines.launch

@Composable
fun ButtonBar(
    state: AuthState,
    password: String,
    setPasswordError: (Int?) -> Unit,
    dispatchAction: (NavigationAction) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val userSettings = LocalUserSettings.current

    Row(modifier = Modifier.fillMaxWidth(1f), Arrangement.SpaceEvenly) {
        AnimatedVisibility(state is AuthState.ConfirmPassword) {
            Button(onClick = {
                dispatchAction(NavigationAction(AuthState.CreatePassword))
            }) {
                Text(text = stringResource(id = R.string.back))
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
                            dispatchAction(NavigationAction(HomeState(), true))
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
