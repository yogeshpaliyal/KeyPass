package com.yogeshpaliyal.keypass.ui.redux.states

import android.content.Context
import com.yogeshpaliyal.keypass.ui.home.DashboardViewModel
import com.yogeshpaliyal.keypass.ui.redux.BottomSheetRoutes

data class KeyPassState(
    val context: Context? = null,
    val viewModel: DashboardViewModel? = null,
    val currentScreen: ScreenState,
    val bottomSheet: BottomSheetState? = null,
    val dialog: DialogState? = null,
    val systemBackPress: Boolean = false
)

fun generateDefaultState(): KeyPassState {
    val currentPage = AuthState.Login
    val bottomSheet = BottomSheetState(BottomSheetRoutes.HOME_NAV_MENU, isBottomSheetOpen = false)
    return KeyPassState(currentScreen = currentPage, bottomSheet = bottomSheet)
}
