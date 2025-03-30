package com.yogeshpaliyal.keypass.ui.redux.actions

import android.content.Context
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.ui.home.DashboardViewModel
import com.yogeshpaliyal.keypass.ui.redux.states.DialogState
import com.yogeshpaliyal.keypass.ui.redux.states.ScreenState

sealed interface Action

/**
 * Batch Multiples Actions to send to ingest in one go
 */
class BatchActions(vararg val actions: Action) : Action

/**
 * Used to update context value in redux store
 */
class UpdateContextAction(val context: Context?) : Action

/**
 * Used to update ViewModal in redux store
 */
class UpdateViewModalAction(val viewModal: DashboardViewModel?) : Action

/**
 * Used to navigate from 1 state to another
 * @param state: ScreenState New State where to navigate
 * @param clearBackStack: Boolean Clear BackStack or not
 */
data class NavigationAction(val state: ScreenState, val clearBackStack: Boolean = false) : Action

/**
 * Used to send Accounts list which we want to restore
 */
data class RestoreAccountsAction(val accounts: List<AccountModel>) : Action

/**
 * Used to update Screen State in redux store
 */
data class StateUpdateAction(val state: ScreenState) : Action

/**
 * used to update dialog state of the app
 * @param dialogState: DialogState? New Dialog State or send null to dismiss the dialog
 */
data class UpdateDialogAction(val dialogState: DialogState? = null) : Action

/**
 * Used to go back to previous screen
 */
object GoBackAction : Action
