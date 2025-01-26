package com.yogeshpaliyal.keypass.ui.redux.actions

import android.os.Bundle
import com.yogeshpaliyal.keypass.ui.redux.BottomSheetRoutes

/**
 * Bottom Sheet action to open bottom sheet for different routes
 */
sealed class BottomSheetAction(
    val route: String,
    val globalIsBottomSheetOpen: Boolean,
    val globalArgs: Bundle? = null
) : Action {
    data class HomeNavigationMenu(val isBottomSheetOpen: Boolean, val args: Bundle? = null) :
        BottomSheetAction(BottomSheetRoutes.HOME_NAV_MENU, isBottomSheetOpen, args)
}
