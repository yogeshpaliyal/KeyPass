package com.yogeshpaliyal.keypass.ui.nav.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import com.yogeshpaliyal.keypass.ui.commonComponents.DefaultBottomAppBar
import com.yogeshpaliyal.keypass.ui.redux.actions.BottomSheetAction
import com.yogeshpaliyal.keypass.ui.redux.actions.NavigationAction
import com.yogeshpaliyal.keypass.ui.redux.states.AccountDetailState
import com.yogeshpaliyal.keypass.ui.redux.states.KeyPassState
import com.yogeshpaliyal.keypass.ui.redux.states.SettingsState
import org.reduxkotlin.compose.rememberDispatcher
import org.reduxkotlin.compose.selectState

@Composable
fun KeyPassBottomBar() {
    val showMainBottomAppBar by selectState<KeyPassState, Boolean> { this.currentScreen.showMainBottomAppBar }
    val dispatchAction = rememberDispatcher()

    if (!showMainBottomAppBar) {
        return
    }

    DefaultBottomAppBar(showBackButton = false, extraAction = {
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
