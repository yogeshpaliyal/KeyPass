package com.yogeshpaliyal.keypass.ui.redux

import android.content.Context
import android.os.Bundle

data class KeyPassState(
    val context: Context? = null,
    val currentScreen: ScreenState,
    val bottomSheet: BottomSheetState,
    val systemBackPress: Boolean = false
)

sealed class ScreenState(val showMainBottomAppBar: Boolean = false)

data class HomeState(val type: String? = null) : ScreenState(true)
data class AccountDetailState(val accountId: Long? = null) : ScreenState()
data class TotpDetailState(val accountId: String? = null) : ScreenState()
object SettingsState : ScreenState(true)

data class BottomSheetState(
    val path: String,
    val args: Bundle? = null,
    val isBottomSheetOpen: Boolean = false
)

fun generateDefaultState(): KeyPassState {
    val currentPage = HomeState()
    val bottomSheet = BottomSheetState(BottomSheetRoutes.HOME_NAV_MENU, isBottomSheetOpen = false)
    return KeyPassState(currentScreen = currentPage, bottomSheet = bottomSheet)
}
