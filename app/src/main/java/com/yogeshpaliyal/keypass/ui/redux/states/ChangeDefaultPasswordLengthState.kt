package com.yogeshpaliyal.keypass.ui.redux.states

import com.yogeshpaliyal.keypass.ui.generate.ui.components.DEFAULT_PASSWORD_LENGTH

data class ChangeDefaultPasswordLengthState(
    val length: Float = DEFAULT_PASSWORD_LENGTH
) : ScreenState(false)
