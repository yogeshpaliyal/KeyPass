package com.yogeshpaliyal.keypass.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.dbhelper.restoreBackup
import com.yogeshpaliyal.common.utils.BACKUP_KEY_LENGTH
import com.yogeshpaliyal.common.utils.email
import com.yogeshpaliyal.common.utils.setBiometricEnable
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.commonComponents.PreferenceItem
import com.yogeshpaliyal.keypass.ui.generate.ui.components.DEFAULT_PASSWORD_LENGTH
import com.yogeshpaliyal.keypass.ui.nav.LocalUserSettings
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.IntentNavigation
import com.yogeshpaliyal.keypass.ui.redux.actions.NavigationAction
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import com.yogeshpaliyal.keypass.ui.redux.states.BackupImporterState
import com.yogeshpaliyal.keypass.ui.redux.states.BackupScreenState
import com.yogeshpaliyal.keypass.ui.redux.states.ChangeAppPasswordState
import com.yogeshpaliyal.keypass.ui.redux.states.ChangeDefaultPasswordLengthState
import kotlinx.coroutines.launch
import org.reduxkotlin.compose.rememberTypedDispatcher

@Composable
fun RestoreDialog(
    selectedFile: Uri,
    hideDialog: () -> Unit,
    saveAccounts: (list: List<AccountModel>) -> Unit
) {
    val (keyphrase, setKeyPhrase) = remember {
        mutableStateOf("")
    }

    val dispatchAction = rememberTypedDispatcher<Action>()

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = {
            hideDialog()
        },
        title = {
            Text(text = stringResource(id = R.string.restore))
        },
        confirmButton = {
            TextButton(onClick = {
                if (keyphrase.isEmpty()) {
                    dispatchAction(ToastAction(R.string.alert_blank_keyphrase))
                    return@TextButton
                }

                if (keyphrase.length != BACKUP_KEY_LENGTH) {
                    dispatchAction(ToastAction(R.string.alert_invalid_keyphrase))
                    return@TextButton
                }
                coroutineScope.launch {
                    val result =
                        restoreBackup(keyphrase, context.contentResolver, selectedFile)

                    if (result != null) {
                        saveAccounts(result)
                        hideDialog()
                        dispatchAction(ToastAction(R.string.backup_restored))
                    } else {
                        dispatchAction(ToastAction(R.string.invalid_keyphrase))
                    }
                }
            }) {
                Text(text = stringResource(id = R.string.restore))
            }
        },
        dismissButton = {
            TextButton(onClick = hideDialog) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(1f)) {
                Text(text = stringResource(id = R.string.keyphrase_restore_info))
                Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(1f),
                    value = keyphrase,
                    onValueChange = setKeyPhrase,
                    placeholder = {
                        Text(text = stringResource(id = R.string.enter_keyphrase))
                    }
                )
            }
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun MySettingCompose() {
    val dispatchAction = rememberTypedDispatcher<Action>()
    val context = LocalContext.current
    val userSettings = LocalUserSettings.current

    // Retrieving saved password length
    var savedPasswordLength by remember { mutableStateOf(DEFAULT_PASSWORD_LENGTH) }
    LaunchedEffect(key1 = Unit) {
        userSettings.defaultPasswordLength.let { value -> savedPasswordLength = value }
    }

    Column {
        PreferenceItem(title = R.string.security, isCategory = true)
        PreferenceItem(
            title = R.string.credentials_backups,
            summary = R.string.credentials_backups_desc
        ) {
            dispatchAction(NavigationAction(BackupScreenState()))
        }
        PreferenceItem(
            title = R.string.restore_credentials,
            summary = R.string.restore_credentials_desc
        ) {
            dispatchAction(NavigationAction(BackupImporterState()))
        }
        PreferenceItem(
            title = R.string.change_app_password,
            summary = R.string.change_app_password,
            icon = Icons.Rounded.Password
        ) {
            dispatchAction(NavigationAction(ChangeAppPasswordState()))
        }
        val changePasswordLengthSummary = context.getString(R.string.default_password_length)
        PreferenceItem(
            title = R.string.change_password_length,
            summaryStr = "$changePasswordLengthSummary: ${savedPasswordLength.toInt()}"
        ) {
            dispatchAction(NavigationAction(ChangeDefaultPasswordLengthState()))
        }

        BiometricsOption()

        Divider(
            modifier = Modifier
                .fillMaxWidth(1f)
                .height(1.dp)
        )
        PreferenceItem(title = R.string.help, isCategory = true)
        PreferenceItem(
            title = R.string.send_feedback,
            summary = R.string.send_feedback_desc,
            icon = Icons.Rounded.Feedback
        ) {
            context.email(
                context.getString(R.string.feedback_to_keypass),
                "yogeshpaliyal.foss@gmail.com"
            )
        }
        PreferenceItem(
            title = R.string.share,
            summary = R.string.share_desc,
            icon = Icons.Rounded.Share
        ) {
            dispatchAction(IntentNavigation.ShareApp)
        }
    }
}

@Composable
fun BiometricsOption() {
    val context = LocalContext.current
    val userSettings = LocalUserSettings.current

    val (canAuthenticate, setCanAuthenticate) = remember {
        mutableStateOf(BiometricManager.BIOMETRIC_STATUS_UNKNOWN)
    }

    val (isBiometricEnable, setIsBiometricEnable) = remember {
        mutableStateOf(false)
    }

    val (subtitle, setSubtitle) = remember {
        mutableStateOf<Int?>(null)
    }

    val dispatch = rememberTypedDispatcher<Action>()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = context) {
        setIsBiometricEnable(userSettings.isBiometricEnable)
    }

    LaunchedEffect(key1 = context) {
        val biometricManager = BiometricManager.from(context)
        setCanAuthenticate(biometricManager.canAuthenticate(BIOMETRIC_STRONG))
    }

    LaunchedEffect(key1 = canAuthenticate, isBiometricEnable) {
        when (canAuthenticate) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                if (isBiometricEnable) {
                    setSubtitle(R.string.enabled)
                } else {
                    setSubtitle(R.string.disabled)
                }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                setSubtitle(R.string.biometric_error_no_hardware)

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                setSubtitle(R.string.biometric_error_hw_unavailable)

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                setSubtitle(R.string.biometric_error_none_enrolled)
            }
        }
    }

    PreferenceItem(
        title = R.string.unlock_with_biometric,
        summary = subtitle,
        icon = Icons.Rounded.Fingerprint
    ) {
        when (canAuthenticate) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                coroutineScope.launch {
                    context.setBiometricEnable(!isBiometricEnable)
                    setIsBiometricEnable(!isBiometricEnable)
                }
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                        )
                    }
                    context.startActivity(enrollIntent)
                } else {
                    dispatch(ToastAction(R.string.password_set_from_settings))
                }
            }
        }
    }
}
