package com.yogeshpaliyal.keypass.ui.auth

import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.yogeshpaliyal.common.utils.getKeyPassPassword
import com.yogeshpaliyal.common.utils.isBiometricEnable
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.auth.components.ButtonBar
import com.yogeshpaliyal.keypass.ui.auth.components.PasswordInputField
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.NavigationAction
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastActionStr
import com.yogeshpaliyal.keypass.ui.redux.states.AuthState
import com.yogeshpaliyal.keypass.ui.redux.states.HomeState
import kotlinx.coroutines.launch
import org.reduxkotlin.compose.rememberDispatcher
import org.reduxkotlin.compose.rememberTypedDispatcher

@Composable
fun AuthScreen(state: AuthState) {
    val context = LocalContext.current

    val dispatchAction = rememberDispatcher()

    val coroutineScope = rememberCoroutineScope()

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
            setBiometricEnable(context.isBiometricEnable())
        }
    }

    BackHandler(state is AuthState.ConfirmPassword) {
        dispatchAction(NavigationAction(AuthState.CreatePassword))
    }

    LaunchedEffect(key1 = Unit, block = {
        coroutineScope.launch {
            val mPassword = context.getKeyPassPassword()
            if (mPassword == null) {
                dispatchAction(NavigationAction(AuthState.CreatePassword))
            }
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

@Composable
fun BiometricPrompt(show: Boolean) {
    if (!show) {
        return
    }

    val context = LocalContext.current
    val dispatch = rememberTypedDispatcher<Action>()

    LaunchedEffect(key1 = context) {
        val fragmentActivity = context as? FragmentActivity ?: return@LaunchedEffect
        val executor = ContextCompat.getMainExecutor(fragmentActivity)
        val biometricPrompt = BiometricPrompt(
            fragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    dispatch(ToastActionStr(context.getString(R.string.authentication_error, errString)))
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    dispatch(NavigationAction(HomeState(), true))
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    dispatch(ToastAction(R.string.authentication_failed))
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.app_name))
            .setSubtitle(context.getString(R.string.login_to_enter_keypass))
            .setAllowedAuthenticators(BIOMETRIC_STRONG or BIOMETRIC_WEAK)
            .setNegativeButtonText(context.getText(R.string.cancel))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
