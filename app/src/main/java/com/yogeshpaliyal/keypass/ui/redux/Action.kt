package com.yogeshpaliyal.keypass.ui.redux

import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.navigation.NavController

sealed interface Action

class UpdateContextAction(val context: Context?) : Action
class UpdateNavControllerAction(val navController: NavController?) : Action

sealed class ScreeNavigationAction(val route: String, val globalArgs: Bundle? = null) : Action {
    data class Home(val tag: String? = null) : ScreeNavigationAction(ScreenRoutes.HOME.replace("{tag}", tag ?: ""))
    data class Settings(val args: Bundle? = null) :
        ScreeNavigationAction(ScreenRoutes.SETTINGS, args)

    data class AddAccount(val accountId: Long? = null) :
        ScreeNavigationAction(ScreenRoutes.ADD_ACCOUNT)

    data class AddTOTP(val accountId: String? = null) :
        ScreeNavigationAction(ScreenRoutes.ADD_TOTP)

    object GeneratePassword :
        ScreeNavigationAction(ScreenRoutes.GENERATE_PASSWORD)
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
