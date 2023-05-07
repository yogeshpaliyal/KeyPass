package com.yogeshpaliyal.keypass.ui.redux.states

import android.os.Bundle

data class BottomSheetState(
    val path: String,
    val args: Bundle? = null,
    val isBottomSheetOpen: Boolean = false
)
