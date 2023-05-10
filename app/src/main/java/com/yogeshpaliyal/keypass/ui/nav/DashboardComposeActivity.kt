package com.yogeshpaliyal.keypass.ui.nav

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.keypass.BuildConfig
import com.yogeshpaliyal.keypass.ui.auth.AuthScreen
import com.yogeshpaliyal.keypass.ui.backup.BackupScreen
import com.yogeshpaliyal.keypass.ui.changePassword.ChangePassword
import com.yogeshpaliyal.keypass.ui.detail.AccountDetailPage
import com.yogeshpaliyal.keypass.ui.home.Homepage
import com.yogeshpaliyal.keypass.ui.redux.KeyPassRedux
import com.yogeshpaliyal.keypass.ui.redux.actions.BottomSheetAction
import com.yogeshpaliyal.keypass.ui.redux.actions.GoBackAction
import com.yogeshpaliyal.keypass.ui.redux.actions.NavigationAction
import com.yogeshpaliyal.keypass.ui.redux.actions.UpdateContextAction
import com.yogeshpaliyal.keypass.ui.redux.states.AccountDetailState
import com.yogeshpaliyal.keypass.ui.redux.states.AuthState
import com.yogeshpaliyal.keypass.ui.redux.states.BackupScreenState
import com.yogeshpaliyal.keypass.ui.redux.states.BottomSheetState
import com.yogeshpaliyal.keypass.ui.redux.states.ChangeAppPasswordState
import com.yogeshpaliyal.keypass.ui.redux.states.HomeState
import com.yogeshpaliyal.keypass.ui.redux.states.KeyPassState
import com.yogeshpaliyal.keypass.ui.redux.states.ScreenState
import com.yogeshpaliyal.keypass.ui.redux.states.SettingsState
import com.yogeshpaliyal.keypass.ui.redux.states.TotpDetailState
import com.yogeshpaliyal.keypass.ui.settings.MySettingCompose
import com.yogeshpaliyal.keypass.ui.style.KeyPassTheme
import dagger.hilt.android.AndroidEntryPoint
import org.reduxkotlin.compose.StoreProvider
import org.reduxkotlin.compose.rememberDispatcher
import org.reduxkotlin.compose.selectState
import java.util.Locale

@AndroidEntryPoint
class DashboardComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG.not()) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        setContent {
            KeyPassTheme {
                StoreProvider(store = KeyPassRedux.createStore()) {
                    Dashboard()
                }
            }
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

    LaunchedEffect(key1 = systemBackPress, block = {
        if (systemBackPress) {
            (context as? ComponentActivity)?.onBackPressed()
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

            OptionBottomBar()
        }
    }
}

@Composable
fun CurrentPage() {
    val currentScreen by selectState<KeyPassState, ScreenState> { this.currentScreen }

    currentScreen.let {
        when (it) {
            is HomeState -> {
                Homepage(homeState = it)
            }

            is SettingsState -> {
                MySettingCompose()
            }

            is AccountDetailState -> {
                AccountDetailPage(id = it.accountId)
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
            is TotpDetailState -> {
            }
        }
    }
}

@Composable
fun OptionBottomBar(
    viewModel: BottomNavViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val bottomSheetState by selectState<KeyPassState, BottomSheetState?> { this.bottomSheet }

    if (bottomSheetState?.isBottomSheetOpen != true) {
        return
    }

    val dispatchAction = rememberDispatcher()

    val navigationItems by viewModel.navigationList.observeAsState()
    ModalBottomSheet(onDismissRequest = {
        dispatchAction(BottomSheetAction.HomeNavigationMenu(false))
    }) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(16.dp)
        ) {
            if (navigationItems != null) {
                items(navigationItems!!) {
                    when (it) {
                        is NavigationModelItem.NavDivider -> {
                            NavItemSection(it)
                        }

                        is NavigationModelItem.NavTagItem -> {
                            NavMenuFolder(folder = it) {
                                dispatchAction(NavigationAction(HomeState(tag = it.tag), false))
                                dispatchAction(BottomSheetAction.HomeNavigationMenu(false))
                            }
                        }

                        is NavigationModelItem.NavMenuItem -> {
                            NavItem(it) {
                                dispatchAction(it.action)
                                dispatchAction(BottomSheetAction.HomeNavigationMenu(false))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavMenuFolder(folder: NavigationModelItem.NavTagItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true
                ),
                onClick = onClick
            )
            .padding(16.dp)
            .fillMaxWidth(1f)

    ) {
        Text(
            text = folder.tag,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun NavItem(item: NavigationModelItem.NavMenuItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = onClick
            )
            .padding(16.dp)
            .fillMaxWidth(1f)
    ) {
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(32.dp))
        Text(
            text = stringResource(id = item.titleRes),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun NavItemSection(divider: NavigationModelItem.NavDivider) {
    Column(modifier = Modifier.padding(16.dp)) {
        Divider()
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = divider.title.uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.labelMedium,
            fontSize = TextUnit(12f, TextUnitType.Sp)
        )
    }
}

@Composable
fun KeyPassBottomBar() {
    val showMainBottomAppBar by selectState<KeyPassState, Boolean> { this.currentScreen.showMainBottomAppBar }
    val dispatchAction = rememberDispatcher()

    if (!showMainBottomAppBar) {
        return
    }

    BottomAppBar(actions = {
        IconButton(onClick = {
            dispatchAction(BottomSheetAction.HomeNavigationMenu(true))
        }) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Rounded.Menu),
                contentDescription = "Menu",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        IconButton(onClick = {
            dispatchAction(NavigationAction(SettingsState))
        }) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Rounded.Settings),
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }, floatingActionButton = {
            FloatingActionButton(modifier = Modifier.testTag("btnAdd"), onClick = {
                dispatchAction(NavigationAction(AccountDetailState()))
            }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.Add),
                    contentDescription = "Add"
                )
            }
        })
}
