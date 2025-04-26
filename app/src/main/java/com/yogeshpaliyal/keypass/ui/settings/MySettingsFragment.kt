package com.yogeshpaliyal.keypass.ui.settings

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.LockReset
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.yogeshpaliyal.common.utils.email
import com.yogeshpaliyal.common.utils.enableAutoFillService
import com.yogeshpaliyal.common.utils.isAutoFillServiceEnabled
import com.yogeshpaliyal.common.utils.setBiometricEnable
import com.yogeshpaliyal.common.utils.setBiometricLoginTimeoutEnable
import com.yogeshpaliyal.common.utils.setUserSettings
import com.yogeshpaliyal.keypass.BuildConfig
import com.yogeshpaliyal.keypass.MyApplication
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.commonComponents.PreferenceItem
import com.yogeshpaliyal.keypass.ui.generate.ui.components.DEFAULT_PASSWORD_LENGTH
import com.yogeshpaliyal.keypass.ui.nav.LocalUserSettings
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.IntentNavigation
import com.yogeshpaliyal.keypass.ui.redux.actions.NavigationAction
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import com.yogeshpaliyal.keypass.ui.redux.actions.UpdateDialogAction
import com.yogeshpaliyal.keypass.ui.redux.states.AboutState
import com.yogeshpaliyal.keypass.ui.redux.states.BackupImporterState
import com.yogeshpaliyal.keypass.ui.redux.states.BackupScreenState
import com.yogeshpaliyal.keypass.ui.redux.states.ChangeAppHintState
import com.yogeshpaliyal.keypass.ui.redux.states.ChangeAppPasswordState
import com.yogeshpaliyal.keypass.ui.redux.states.ChangeDefaultPasswordLengthState
import com.yogeshpaliyal.keypass.ui.redux.states.ValidateKeyPhrase
import kotlinx.coroutines.launch
import org.reduxkotlin.compose.rememberTypedDispatcher

/**
 * Represents a setting category with its preferences
 */
data class SettingsCategory(
    val titleRes: Int,
    val preferences: List<SettingsPreference>
)

/**
 * Represents a single preference item
 */
data class SettingsPreference(
    val type: PreferenceType,
    val titleRes: Int,
    val summaryRes: Int? = null,
    val iconRes: Any? = null,
    val onClick: (() -> Unit)? = null,
    val isVisible: Boolean = true,
    val summaryStr: String? = null
)

enum class PreferenceType {
    NORMAL, AUTO_FILL, BIOMETRIC, AUTO_LOCK, AUTO_DISABLE_BIOMETRIC
}

@Composable
fun MySettingCompose() {
  val dispatchAction = rememberTypedDispatcher<Action>()
  val context = LocalContext.current
  val userSettings = LocalUserSettings.current
  var isAutoFillServiceEnable by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  // Search functionality
  var searchQuery by remember { mutableStateOf("") }

  // Expandable sections
  var isSecurityExpanded by remember { mutableStateOf(true) }
  var isHelpExpanded by remember { mutableStateOf(true) }

  // Retrieving saved password length
  var savedPasswordLength by remember { mutableStateOf(DEFAULT_PASSWORD_LENGTH) }
  LaunchedEffect(key1 = Unit) {
    userSettings.passwordConfig.length.let { value -> savedPasswordLength = value }
  }

  LaunchedEffect(context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      isAutoFillServiceEnable = context.isAutoFillServiceEnabled()
    }
  }

  // Biometrics related states
  var canAuthenticate by remember { mutableStateOf(BiometricManager.BIOMETRIC_STATUS_UNKNOWN) }
  var isBiometricEnable by remember { mutableStateOf(false) }
  var biometricSubtitle by remember { mutableStateOf<Int?>(null) }

  LaunchedEffect(key1 = context) { 
    isBiometricEnable = userSettings.isBiometricEnable 
    val biometricManager = BiometricManager.from(context)
    canAuthenticate = biometricManager.canAuthenticate(BIOMETRIC_STRONG)
  }

  LaunchedEffect(key1 = canAuthenticate, isBiometricEnable) {
    biometricSubtitle = when (canAuthenticate) {
      BiometricManager.BIOMETRIC_SUCCESS ->
          if (isBiometricEnable) R.string.enabled else R.string.disabled
      BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
          R.string.biometric_error_no_hardware
      BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
          R.string.biometric_error_hw_unavailable
      BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
          R.string.biometric_error_none_enrolled
      else -> null
    }
  }

  // Create settings categories
  val securitySettings = SettingsCategory(
    titleRes = R.string.security,
    preferences = listOf(
      SettingsPreference(
        type = PreferenceType.NORMAL,
        titleRes = R.string.credentials_backups,
        summaryRes = R.string.credentials_backups_desc,
        iconRes = painterResource(id = R.drawable.credentials_backup),
        onClick = { dispatchAction(NavigationAction(BackupScreenState())) }
      ),
      SettingsPreference(
        type = PreferenceType.NORMAL,
        titleRes = R.string.restore_credentials,
        summaryRes = R.string.restore_credentials_desc,
        iconRes = painterResource(id = R.drawable.import_credentials),
        onClick = { dispatchAction(NavigationAction(BackupImporterState())) }
      ),
      SettingsPreference(
        type = PreferenceType.NORMAL,
        titleRes = R.string.change_app_password,
        summaryRes = R.string.change_app_password,
        iconRes = Icons.Rounded.Password,
        onClick = { dispatchAction(NavigationAction(ChangeAppPasswordState())) }
      ),
      SettingsPreference(
        type = PreferenceType.NORMAL,
        titleRes = R.string.app_password_hint,
        summaryRes = if (userSettings.passwordHint != null) R.string.change_app_password_hint else R.string.set_app_password_hint,
        iconRes = Icons.Outlined.Info,
        onClick = { dispatchAction(NavigationAction(ChangeAppHintState)) }
      ),
      SettingsPreference(
        type = PreferenceType.NORMAL,
        titleRes = R.string.change_password_length,
        summaryStr = "${context.getString(R.string.default_password_length)}: ${savedPasswordLength.toInt()}",
        onClick = { dispatchAction(NavigationAction(ChangeDefaultPasswordLengthState())) }
      ),
      SettingsPreference(
        type = PreferenceType.NORMAL,
        titleRes = R.string.validate_keyphrase,
        summaryRes = R.string.validate_keyphrase,
        onClick = { dispatchAction(UpdateDialogAction(ValidateKeyPhrase)) }
      ),
      SettingsPreference(
        type = PreferenceType.AUTO_FILL,
        titleRes = R.string.autofill_service,
        summaryRes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          if (isAutoFillServiceEnable) R.string.autofill_service_disable else R.string.autofill_service_enable
        } else R.string.autofill_not_available
      ),
      SettingsPreference(
        type = PreferenceType.BIOMETRIC,
        titleRes = R.string.unlock_with_biometric,
        summaryRes = biometricSubtitle,
        iconRes = Icons.Rounded.Fingerprint
      ),
      SettingsPreference(
        type = PreferenceType.AUTO_DISABLE_BIOMETRIC,
        titleRes = R.string.biometric_login_timeout,
        summaryRes = if (userSettings.biometricLoginTimeoutEnable == true) R.string.enabled else R.string.disabled,
        iconRes = Icons.Rounded.LockReset,
        isVisible = userSettings.isBiometricEnable
      ),
      SettingsPreference(
        type = PreferenceType.AUTO_LOCK,
        titleRes = R.string.auto_lock,
        summaryRes = if (userSettings.autoLockEnabled == true) R.string.enabled else R.string.disabled,
        iconRes = Icons.Rounded.LockReset
      )
    )
  )

  val helpSettings = SettingsCategory(
    titleRes = R.string.help,
    preferences = listOf(
      SettingsPreference(
        type = PreferenceType.NORMAL,
        titleRes = R.string.send_feedback,
        summaryRes = R.string.send_feedback_desc,
        iconRes = Icons.Rounded.Feedback,
        onClick = { context.email(context.getString(R.string.feedback_to_keypass), "yogeshpaliyal.foss@gmail.com") }
      ),
      SettingsPreference(
        type = PreferenceType.NORMAL,
        titleRes = R.string.share,
        summaryRes = R.string.share_desc,
        iconRes = Icons.Rounded.Share,
        onClick = { dispatchAction(IntentNavigation.ShareApp) }
      ),
      SettingsPreference(
        type = PreferenceType.NORMAL,
        titleRes = R.string.about_us,
        summaryRes = R.string.about_us,
        iconRes = Icons.Outlined.Info,
        onClick = { dispatchAction(NavigationAction(AboutState())) }
      )
    )
  )

  // Filter preferences based on search
  val filteredSecuritySettings = remember(searchQuery, securitySettings) {
    derivedStateOf {
      if (searchQuery.isEmpty()) {
        securitySettings.preferences
      } else {
        securitySettings.preferences.filter { preference ->
          val titleMatches = context.getString(preference.titleRes).contains(searchQuery, ignoreCase = true)
          val summaryMatches = preference.summaryRes?.let { 
            context.getString(it).contains(searchQuery, ignoreCase = true) 
          } ?: false
          val summaryStrMatches = preference.summaryStr?.contains(searchQuery, ignoreCase = true) ?: false
          
          titleMatches || summaryMatches || summaryStrMatches
        }
      }
    }
  }

  val filteredHelpSettings = remember(searchQuery, helpSettings) {
    derivedStateOf {
      if (searchQuery.isEmpty()) {
        helpSettings.preferences
      } else {
        helpSettings.preferences.filter { preference ->
          val titleMatches = context.getString(preference.titleRes).contains(searchQuery, ignoreCase = true)
          val summaryMatches = preference.summaryRes?.let { 
            context.getString(it).contains(searchQuery, ignoreCase = true) 
          } ?: false
          
          titleMatches || summaryMatches
        }
      }
    }
  }

  Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    // Search bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      placeholder = { Text(text = "Search settings...") },
      leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
      singleLine = true,
      shape = MaterialTheme.shapes.medium
    )

    // Security section
    Card(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = context.getString(R.string.security),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.primary
        )
        IconButton(onClick = { isSecurityExpanded = !isSecurityExpanded }) {
          Icon(
            imageVector = if (isSecurityExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = if (isSecurityExpanded) "Collapse" else "Expand"
          )
        }
      }
      
      if (isSecurityExpanded) {
        Column(modifier = Modifier.fillMaxWidth()) {
          filteredSecuritySettings.value.forEach { preference ->
            when (preference.type) {
              PreferenceType.NORMAL -> {
                PreferenceItem(
                  painter = preference.iconRes as? androidx.compose.ui.graphics.painter.Painter,
                  icon = preference.iconRes as? androidx.compose.ui.graphics.vector.ImageVector,
                  title = preference.titleRes,
                  summary = preference.summaryRes,
                  summaryStr = preference.summaryStr,
                  onClickItem = preference.onClick
                )
              }
              PreferenceType.AUTO_FILL -> {
                val autoFillClick = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                  {
                    (context.applicationContext as? MyApplication)?.activityLaunchTriggered()
                    context.enableAutoFillService()
                  }
                } else null
                
                PreferenceItem(
                  title = preference.titleRes,
                  summary = preference.summaryRes,
                  onClickItem = autoFillClick
                )
              }
              PreferenceType.BIOMETRIC -> {
                PreferenceItem(
                  title = preference.titleRes,
                  summary = preference.summaryRes,
                  icon = preference.iconRes as? androidx.compose.ui.graphics.vector.ImageVector
                ) {
                  when (canAuthenticate) {
                    BiometricManager.BIOMETRIC_SUCCESS -> {
                      coroutineScope.launch {
                        context.setBiometricEnable(!isBiometricEnable)
                        isBiometricEnable = !isBiometricEnable
                      }
                    }
                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                          putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                          )
                        }
                        context.startActivity(enrollIntent)
                      } else {
                        dispatchAction(ToastAction(R.string.password_set_from_settings))
                      }
                    }
                  }
                }
              }
              PreferenceType.AUTO_LOCK -> {
                PreferenceItem(
                  title = preference.titleRes,
                  summary = preference.summaryRes,
                  icon = preference.iconRes as? androidx.compose.ui.graphics.vector.ImageVector
                ) {
                  coroutineScope.launch {
                    context.setUserSettings(
                      userSettings.copy(autoLockEnabled = userSettings.autoLockEnabled == false)
                    )
                  }
                }
              }
              PreferenceType.AUTO_DISABLE_BIOMETRIC -> {
                if (preference.isVisible) {
                  PreferenceItem(
                    title = preference.titleRes,
                    summary = preference.summaryRes,
                    icon = preference.iconRes as? androidx.compose.ui.graphics.vector.ImageVector
                  ) {
                    coroutineScope.launch {
                      context.setBiometricLoginTimeoutEnable(
                        userSettings.biometricLoginTimeoutEnable != true
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // Help section
    Card(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = context.getString(R.string.help),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.primary
        )
        IconButton(onClick = { isHelpExpanded = !isHelpExpanded }) {
          Icon(
            imageVector = if (isHelpExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = if (isHelpExpanded) "Collapse" else "Expand"
          )
        }
      }
      
      if (isHelpExpanded) {
        Column(modifier = Modifier.fillMaxWidth()) {
          filteredHelpSettings.value.forEach { preference ->
            PreferenceItem(
              painter = preference.iconRes as? androidx.compose.ui.graphics.painter.Painter,
              icon = preference.iconRes as? androidx.compose.ui.graphics.vector.ImageVector,
              title = preference.titleRes,
              summary = preference.summaryRes,
              onClickItem = preference.onClick
            )
          }
        }
      }
    }

    // App version
    Card(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Text(
        text = "App Version ${BuildConfig.VERSION_NAME}",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
      )
    }
  }
}

// The existing helper functions (AutoFillPreferenceItem, BiometricsOption, AutoLockPreferenceItem, AutoDisableBiometric)
// are no longer needed as their functionality has been integrated into the main composable
