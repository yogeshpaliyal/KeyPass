package com.yogeshpaliyal.keypass.ui.nav

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.yogeshpaliyal.common.data.UserSettings
import com.yogeshpaliyal.common.utils.getUserSettings
import com.yogeshpaliyal.common.utils.getUserSettingsFlow
import com.yogeshpaliyal.common.utils.migrateOldDataToNewerDataStore
import com.yogeshpaliyal.common.utils.setUserSettings
import com.yogeshpaliyal.keypass.BuildConfig
import com.yogeshpaliyal.keypass.ui.about.AboutScreen
import com.yogeshpaliyal.keypass.ui.auth.AuthScreen
import com.yogeshpaliyal.keypass.ui.backup.BackupScreen
import com.yogeshpaliyal.keypass.ui.backupsImport.BackupImporter
import com.yogeshpaliyal.keypass.ui.changeDefaultPasswordLength.ChangeDefaultPasswordLengthScreen
import com.yogeshpaliyal.keypass.ui.changePassword.ChangePassword
import com.yogeshpaliyal.keypass.ui.detail.AccountDetailPage
import com.yogeshpaliyal.keypass.ui.generate.ui.GeneratePasswordScreen
import com.yogeshpaliyal.keypass.ui.home.Homepage
import com.yogeshpaliyal.keypass.ui.home.components.ValidateKeyPhraseDialog
import com.yogeshpaliyal.keypass.ui.nav.components.DashboardBottomSheet
import com.yogeshpaliyal.keypass.ui.nav.components.KeyPassBottomBar
import com.yogeshpaliyal.keypass.ui.passwordHint.PasswordHintScreen
import com.yogeshpaliyal.keypass.ui.redux.KeyPassRedux
import com.yogeshpaliyal.keypass.ui.redux.actions.GoBackAction
import com.yogeshpaliyal.keypass.ui.redux.actions.UpdateContextAction
import com.yogeshpaliyal.keypass.ui.redux.states.AboutState
import com.yogeshpaliyal.keypass.ui.redux.states.AccountDetailState
import com.yogeshpaliyal.keypass.ui.redux.states.AuthState
import com.yogeshpaliyal.keypass.ui.redux.states.BackupImporterState
import com.yogeshpaliyal.keypass.ui.redux.states.BackupScreenState
import com.yogeshpaliyal.keypass.ui.redux.states.ChangeAppHintState
import com.yogeshpaliyal.keypass.ui.redux.states.ChangeAppPasswordState
import com.yogeshpaliyal.keypass.ui.redux.states.ChangeDefaultPasswordLengthState
import com.yogeshpaliyal.keypass.ui.redux.states.HomeState
import com.yogeshpaliyal.keypass.ui.redux.states.KeyPassState
import com.yogeshpaliyal.keypass.ui.redux.states.PasswordGeneratorState
import com.yogeshpaliyal.keypass.ui.redux.states.SettingsState
import com.yogeshpaliyal.keypass.ui.redux.states.ValidateKeyPhrase
import com.yogeshpaliyal.keypass.ui.settings.MySettingCompose
import com.yogeshpaliyal.keypass.ui.style.KeyPassTheme
import dagger.hilt.android.AndroidEntryPoint
import org.reduxkotlin.compose.StoreProvider
import org.reduxkotlin.compose.rememberDispatcher
import org.reduxkotlin.compose.selectState

val LocalUserSettings = compositionLocalOf { UserSettings() }

@AndroidEntryPoint
class DashboardComposeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG.not()) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }

        setContent {
            val localUserSettings by getUserSettingsFlow().collectAsState(initial = UserSettings())

            CompositionLocalProvider(LocalUserSettings provides localUserSettings) {
                KeyPassTheme {
                    StoreProvider(store = KeyPassRedux.createStore()) {
                        Dashboard()
                    }
                }
            }

            LaunchedEffect(key1 = Unit, block = {
                migrateOldDataToNewerDataStore()
                val userSettings = getUserSettings()
                val buildConfigVersion = BuildConfig.VERSION_CODE
                val currentAppVersion = userSettings.currentAppVersion
                if (buildConfigVersion != currentAppVersion) {
                    applicationContext.setUserSettings(
                        userSettings.copy(
                            lastAppVersion = currentAppVersion,
                            currentAppVersion = buildConfigVersion
                        )
                    )
                }
            })
        }
    }
}

@Composable
fun Dashboard() {
    val systemBackPress by selectState<KeyPassState, Boolean> { this.systemBackPress }

    val context = LocalContext.current
    val dispatch = rememberDispatcher()

    BackHandler(!systemBackPress) {
        dispatch(GoBackAction)
    }

    // Call this like any other SideEffect in your composable
//    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
//        dispatch(NavigationAction(AuthState.Login))
//    }

    LaunchedEffect(key1 = systemBackPress, block = {
        if (systemBackPress) {
            (context as? ComponentActivity)?.finishAffinity()
        }
    })

    DisposableEffect(KeyPassRedux, context) {
        dispatch(UpdateContextAction(context))

        onDispose {
            dispatch(UpdateContextAction(null))
        }
    }

    Scaffold(bottomBar = {
        KeyPassBottomBar()
    }) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            CurrentPage()

            DashboardBottomSheet()
        }
    }
}

@Composable
fun CurrentPage() {
    val currentScreen by selectState<KeyPassState, KeyPassState> { this }

//    val currentDialog by selectState<KeyPassState, DialogState?> { this.dialog }

    currentScreen.currentScreen.let {
        when (it) {
            is HomeState -> {
                Homepage(homeState = it)
            }

            is SettingsState -> {
                MySettingCompose()
            }

            is AccountDetailState -> {
                AccountDetailPage(it.accountId)
            }

            is AuthState -> {
                AuthScreen(it)
            }

            is BackupScreenState -> {
                BackupScreen(state = it)
            }

            is ChangeAppPasswordState -> {
                ChangePassword(it)
            }

            is ChangeDefaultPasswordLengthState -> {
                ChangeDefaultPasswordLengthScreen()
            }

            is BackupImporterState -> BackupImporter(state = it)
            is AboutState -> AboutScreen()
            is PasswordGeneratorState -> GeneratePasswordScreen()
            is ChangeAppHintState -> PasswordHintScreen()
        }
    }

    currentScreen.dialog?.let {
        when (it) {
            is ValidateKeyPhrase -> {
                ValidateKeyPhraseDialog()
            }
        }
    }
}
