package com.yogeshpaliyal.keypass.ui.redux.actions

import android.content.Context
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.ui.home.DashboardViewModel
import com.yogeshpaliyal.keypass.ui.redux.states.DialogState
import com.yogeshpaliyal.keypass.ui.redux.states.ScreenState

sealed interface Action

class UpdateContextAction(val context: Context?) : Action
class UpdateViewModalAction(val viewModal: DashboardViewModel?) : Action

data class NavigationAction(val state: ScreenState, val clearBackStack: Boolean = false) : Action

data class RestoreAccountsAction(val accounts: List<AccountModel>) : Action

data class StateUpdateAction(val state: ScreenState) : Action
data class UpdateDialogAction(val dialogState: DialogState? = null) : Action
