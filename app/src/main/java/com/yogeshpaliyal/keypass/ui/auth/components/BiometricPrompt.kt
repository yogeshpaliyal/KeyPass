package com.yogeshpaliyal.keypass.ui.auth.components

import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.NavigationAction
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastActionStr
import com.yogeshpaliyal.keypass.ui.redux.states.HomeState
import com.yogeshpaliyal.keypass.ui.nav.LocalUserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.reduxkotlin.compose.rememberTypedDispatcher

@Composable
fun BiometricPrompt(show: Boolean, onDismiss: () -> Unit) {
    if (!show) {
        return
    }

    val context = LocalContext.current
    val dispatch = rememberTypedDispatcher<Action>()
    val userSettings = LocalUserSettings.current

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
                    onDismiss()
                    dispatch(
                        ToastActionStr(
                            context.getString(
                                R.string.authentication_error,
                                errString
                            )
                        )
                    )
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    dispatch(NavigationAction(HomeState(), true))
                    updateLastBiometricLoginTime(context)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onDismiss()
                    dispatch(ToastAction(R.string.authentication_failed))
                }
            }
        )

        val lastPasswordLoginTime = userSettings.lastPasswordLoginTime
        val currentTime = System.currentTimeMillis()
        if (lastPasswordLoginTime != null && currentTime - lastPasswordLoginTime > 24 * 60 * 60 * 1000) {
            onDismiss()
            dispatch(ToastAction(R.string.biometric_disabled_due_to_timeout))
            return@LaunchedEffect
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.app_name))
            .setSubtitle(context.getString(R.string.login_to_enter_keypass))
            .setAllowedAuthenticators(BIOMETRIC_STRONG or BIOMETRIC_WEAK)
            .setNegativeButtonText(context.getText(R.string.cancel))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}

suspend fun updateLastBiometricLoginTime(context: Context) {
    withContext(Dispatchers.IO) {
        val userSettings = context.getUserSettings()
        context.setUserSettings(userSettings.copy(lastBiometricLoginTime = System.currentTimeMillis()))
    }
}
