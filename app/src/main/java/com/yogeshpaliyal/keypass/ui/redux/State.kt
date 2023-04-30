package com.yogeshpaliyal.keypass.ui.redux

import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import com.yogeshpaliyal.keypass.R

data class KeyPassState(
    val context: Context? = null,
    val currentScreen: ScreenState,
    val bottomSheet: BottomSheetState? = null,
    val systemBackPress: Boolean = false
)

sealed class ScreenState(val showMainBottomAppBar: Boolean = false)

data class HomeState(val keyword: String? = null, val tag: String? = null, val sortField: String? = null, val sortAscending: Boolean = true) : ScreenState(true)
data class AccountDetailState(val accountId: Long? = null) : ScreenState()
data class TotpDetailState(val accountId: String? = null) : ScreenState()
object SettingsState : ScreenState(true)

open class AuthState(@StringRes val title: Int) : ScreenState(false) {
    object CreatePassword : AuthState(R.string.create_password)
    class ConfirmPassword(val password: String) : AuthState(R.string.confirm_password)
    object Login : AuthState(R.string.login_to_enter_keypass)
}
data class BottomSheetState(
    val path: String,
    val args: Bundle? = null,
    val isBottomSheetOpen: Boolean = false
)

fun generateDefaultState(): KeyPassState {
    val currentPage = AuthState.Login
    val bottomSheet = BottomSheetState(BottomSheetRoutes.HOME_NAV_MENU, isBottomSheetOpen = false)
    return KeyPassState(currentScreen = currentPage, bottomSheet = bottomSheet)
}
