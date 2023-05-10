package com.yogeshpaliyal.keypass.ui.redux.states

import androidx.annotation.StringRes

data class ChangeAppPasswordState(
    val oldPassword: PasswordFieldState = PasswordFieldState(),
    val newPassword: PasswordFieldState = PasswordFieldState(),
    val confirmPassword: PasswordFieldState = PasswordFieldState()
) : ScreenState(false)

data class PasswordFieldState(
    val password: String = "",
    val passwordVisible: Boolean = false,
    @StringRes val passwordError: Int? = null
)
