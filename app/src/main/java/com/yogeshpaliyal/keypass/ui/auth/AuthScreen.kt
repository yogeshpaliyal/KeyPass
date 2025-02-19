package com.yogeshpaliyal.keypass.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.auth.components.ButtonBar
import com.yogeshpaliyal.keypass.ui.auth.components.PasswordInputField
import com.yogeshpaliyal.keypass.ui.nav.LocalUserSettings
import com.yogeshpaliyal.keypass.ui.redux.actions.NavigationAction
import com.yogeshpaliyal.keypass.ui.redux.states.AuthState
import org.reduxkotlin.compose.rememberDispatcher

@Composable
fun AuthScreen(state: AuthState) {
    val userSettings = LocalUserSettings.current
    val dispatchAction = rememberDispatcher()

    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }
    val passwordError = rememberSaveable { mutableStateOf<Int?>(null) }

    BackHandler(state is AuthState.ConfirmPassword) {
        dispatchAction(NavigationAction(AuthState.CreatePassword, true))
    }

    LaunchedEffect(key1 = userSettings.keyPassPassword) {
        val mPassword = userSettings.keyPassPassword
        if (mPassword == null) {
            dispatchAction(NavigationAction(AuthState.CreatePassword, true))
        } else {
            dispatchAction(NavigationAction(AuthState.Login, true))
        }
    }

    Column(
        modifier = Modifier
            .padding(32.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_undraw_unlock_24mb),
            contentDescription = null
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = state.title),
                fontWeight = FontWeight.Bold
            )

            if (state.description != null) {
                Text(
                    text = stringResource(id = state.description),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        PasswordInputField(
            password = password.value,
            setPassword = { password.value = it },
            passwordVisible = passwordVisible.value,
            setPasswordVisible = { passwordVisible.value = it },
            passwordError = passwordError.value,
            hint = if (state is AuthState.Login && userSettings.passwordHint != null)
                userSettings.passwordHint
            else null
        )

        ButtonBar(
            state = state,
            password = password.value,
            setPasswordError = { passwordError.value = it }
        ) {
            dispatchAction(it)
        }
    }
}
