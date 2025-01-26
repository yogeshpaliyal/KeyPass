package com.yogeshpaliyal.keypass.ui.redux.actions

import androidx.annotation.StringRes
import com.yogeshpaliyal.keypass.R

interface UtilityAction : Action

data class ToastAction(@StringRes val text: Int) : UtilityAction
data class ToastActionStr(val text: String) : UtilityAction
data class CopyToClipboard(val password: String, @StringRes val successMessage: Int = R.string.copied_to_clipboard) : UtilityAction
