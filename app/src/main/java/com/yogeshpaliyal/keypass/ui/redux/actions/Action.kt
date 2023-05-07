package com.yogeshpaliyal.keypass.ui.redux.actions

import android.content.Context
import com.yogeshpaliyal.keypass.ui.redux.states.ScreenState

sealed interface Action

class UpdateContextAction(val context: Context?) : Action

data class NavigationAction(val state: ScreenState, val clearBackStack: Boolean = false) : Action
data class StateUpdateAction(val state: ScreenState) : Action
