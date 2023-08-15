package com.yogeshpaliyal.keypass.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.auth.components.BiometricPrompt
import com.yogeshpaliyal.keypass.ui.auth.components.ButtonBar
import com.yogeshpaliyal.keypass.ui.auth.components.PasswordInputField
import com.yogeshpaliyal.keypass.ui.nav.LocalUserSettings
import com.yogeshpaliyal.keypass.ui.redux.actions.NavigationAction
import com.yogeshpaliyal.keypass.ui.redux.states.AuthState
import org.reduxkotlin.compose.rememberDispatcher

@Composable
fun AuthScreen(state: AuthState) {
    val context = LocalContext.current
    val userSettings = LocalUserSettings.current

    val dispatchAction = rememberDispatcher()

    val (password, setPassword) = remember(state) {
        mutableStateOf("")
    }

    val (passwordVisible, setPasswordVisible) = remember(state) { mutableStateOf(false) }

    val (passwordError, setPasswordError) = remember(state, password) {
        mutableStateOf<Int?>(null)
    }

    val (biometricEnable, setBiometricEnable) = remember(state) { mutableStateOf(false) }

    LaunchedEffect(key1 = context, state) {
        if (state is AuthState.Login) {
            setBiometricEnable(userSettings.isBiometricEnable)
        }
    }

    BackHandler(state is AuthState.ConfirmPassword) {
        dispatchAction(NavigationAction(AuthState.CreatePassword, true))
    }

    LaunchedEffect(key1 = userSettings.keyPassPassword, block = {
        val mPassword = userSettings.keyPassPassword
        if (mPassword == null) {
            dispatchAction(NavigationAction(AuthState.CreatePassword, true))
        } else {
            dispatchAction(NavigationAction(AuthState.Login, true))
        }
    })

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

        Text(text = stringResource(id = state.title))

        PasswordInputField(
            password = password,
            setPassword = setPassword,
            passwordVisible = passwordVisible,
            setPasswordVisible = setPasswordVisible,
            passwordError = passwordError
        )

        ButtonBar(state, password, setPasswordError) {
            dispatchAction(it)
        }
    }

    BiometricPrompt(show = biometricEnable)
}
