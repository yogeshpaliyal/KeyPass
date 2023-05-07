package com.yogeshpaliyal.keypass.ui.redux.actions

import androidx.annotation.StringRes

data class ToastAction(@StringRes val text: Int) : Action
data class CopyToClipboard(val password: String) : Action

object GoBackAction : Action
