package com.yogeshpaliyal.keypass.ui.redux

import android.content.Context
import android.os.Bundle
import androidx.navigation.NavController

data class KeyPassState(
    val context: Context? = null,
    val navController: NavController? = null,
    val currentScreen: ScreenState,
    val bottomSheet: BottomSheetState
)

data class ScreenState(
    val path: String,
    val args: Bundle? = null
)

data class BottomSheetState(
    val path: String,
    val args: Bundle? = null,
    val isBottomSheetOpen: Boolean = false
)

fun generateDefaultState(): KeyPassState {
    val screenState = ScreenState(ScreenRoutes.HOME)
    val bottomSheet = BottomSheetState(BottomSheetRoutes.HOME_NAV_MENU, isBottomSheetOpen = false)
    return KeyPassState(currentScreen = screenState, bottomSheet = bottomSheet)
}
