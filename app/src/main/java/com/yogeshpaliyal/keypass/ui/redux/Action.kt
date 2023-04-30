package com.yogeshpaliyal.keypass.ui.redux

import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes

sealed interface Action

class UpdateContextAction(val context: Context?) : Action

object GoBackAction : Action

data class NavigationAction(val state: ScreenState, val clearBackStack: Boolean = false) : Action
data class StateUpdateAction(val state: ScreenState) : Action

sealed interface IntentNavigation : Action {
    object GeneratePassword : IntentNavigation
    object BackupActivity : IntentNavigation
    object ShareApp : IntentNavigation
    data class AddTOTP(val accountId: String? = null) : IntentNavigation
}

data class ToastAction(@StringRes val text: Int) : Action
data class CopyToClipboard(val password: String) : Action

sealed class BottomSheetAction(
    val route: String,
    val globalIsBottomSheetOpen: Boolean,
    val globalArgs: Bundle? = null
) : Action {
    data class HomeNavigationMenu(val isBottomSheetOpen: Boolean, val args: Bundle? = null) :
        BottomSheetAction(BottomSheetRoutes.HOME_NAV_MENU, isBottomSheetOpen, args)
}
