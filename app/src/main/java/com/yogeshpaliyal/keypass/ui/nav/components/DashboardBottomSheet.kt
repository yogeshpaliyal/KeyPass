package com.yogeshpaliyal.keypass.ui.nav.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.keypass.ui.nav.BottomNavViewModel
import com.yogeshpaliyal.keypass.ui.nav.NavigationModelItem
import com.yogeshpaliyal.keypass.ui.redux.actions.BottomSheetAction
import com.yogeshpaliyal.keypass.ui.redux.actions.NavigationAction
import com.yogeshpaliyal.keypass.ui.redux.states.BottomSheetState
import com.yogeshpaliyal.keypass.ui.redux.states.HomeState
import com.yogeshpaliyal.keypass.ui.redux.states.KeyPassState
import org.reduxkotlin.compose.rememberDispatcher
import org.reduxkotlin.compose.selectState

@Composable
fun DashboardBottomSheet(viewModel: BottomNavViewModel) {
    val bottomSheetState by selectState<KeyPassState, BottomSheetState?> { this.bottomSheet }

    if (bottomSheetState?.isBottomSheetOpen != true) {
        return
    }

    OptionBottomBar(viewModel)
}

@Composable
fun OptionBottomBar(
    viewModel: BottomNavViewModel
) {
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
