package com.yogeshpaliyal.keypass.ui.redux.states

import androidx.annotation.StringRes
import com.yogeshpaliyal.keypass.R

open class AuthState(@StringRes val title: Int, @StringRes val description: Int? = null) : ScreenState(false) {
    object CreatePassword : AuthState(R.string.create_password, R.string.create_password_description)
    class ConfirmPassword(val password: String) : AuthState(R.string.confirm_password, R.string.confirm_password_description)
    object Login : AuthState(R.string.login_to_enter_keypass)
}
