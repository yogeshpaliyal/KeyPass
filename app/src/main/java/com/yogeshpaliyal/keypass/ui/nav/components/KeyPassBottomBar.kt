package com.yogeshpaliyal.keypass.ui.nav.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import com.yogeshpaliyal.keypass.ui.commonComponents.DefaultBottomAppBar
import com.yogeshpaliyal.keypass.ui.nav.BottomNavViewModel
import com.yogeshpaliyal.keypass.ui.redux.actions.BottomSheetAction
import com.yogeshpaliyal.keypass.ui.redux.actions.IntentNavigation
import com.yogeshpaliyal.keypass.ui.redux.actions.NavigationAction
import com.yogeshpaliyal.keypass.ui.redux.states.AccountDetailState
import com.yogeshpaliyal.keypass.ui.redux.states.HomeState
import com.yogeshpaliyal.keypass.ui.redux.states.KeyPassState
import com.yogeshpaliyal.keypass.ui.redux.states.ScreenState
import com.yogeshpaliyal.keypass.ui.redux.states.SettingsState
import org.reduxkotlin.compose.rememberDispatcher
import org.reduxkotlin.compose.selectState




@Composable
fun KeyPassBottomBar(viewModel: BottomNavViewModel) {
    val currentScreen: ScreenState by selectState<KeyPassState, ScreenState> { this.currentScreen }
    val showMainBottomAppBar = currentScreen.showMainBottomAppBar
    val dispatchAction = rememberDispatcher()
    val navigationItems by viewModel.navigationList.observeAsState()

    if (!showMainBottomAppBar) {
        return
    }

    DefaultBottomAppBar(showBackButton = false, extraAction = {

        IconToggleButton (colors = IconButtonDefaults.iconToggleButtonColors(
            checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ), checked = currentScreen is HomeState, onCheckedChange = {
            dispatchAction(NavigationAction(HomeState(), true))
        }) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Outlined.Home),
                contentDescription = "Home",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        IconButton(onClick = {
            dispatchAction(IntentNavigation.GeneratePassword)
        }) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Outlined.VpnKey),
                contentDescription = "Generate Password",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        if (navigationItems?.isNotEmpty() == true) {
            IconButton(onClick = {
                dispatchAction(BottomSheetAction.HomeNavigationMenu(true))
            }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Outlined.Menu),
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        IconToggleButton(colors = IconButtonDefaults.iconToggleButtonColors(
            checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ), checked = currentScreen is SettingsState, onCheckedChange = {
            dispatchAction(NavigationAction(SettingsState))
        }) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Outlined.Settings),
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
