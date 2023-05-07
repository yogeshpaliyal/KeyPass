package com.yogeshpaliyal.keypass.ui.redux.states

import androidx.annotation.StringRes
import com.yogeshpaliyal.keypass.R

open class AuthState(@StringRes val title: Int) : ScreenState(false) {
    object CreatePassword : AuthState(R.string.create_password)
    class ConfirmPassword(val password: String) : AuthState(R.string.confirm_password)
    object Login : AuthState(R.string.login_to_enter_keypass)
}
