package com.yogeshpaliyal.keypass.ui.redux

import com.yogeshpaliyal.keypass.ui.redux.actions.BatchActions
import com.yogeshpaliyal.keypass.ui.redux.actions.BottomSheetAction
import com.yogeshpaliyal.keypass.ui.redux.actions.GoBackAction
import com.yogeshpaliyal.keypass.ui.redux.actions.NavigationAction
import com.yogeshpaliyal.keypass.ui.redux.actions.RestoreAccountsAction
import com.yogeshpaliyal.keypass.ui.redux.actions.StateUpdateAction
import com.yogeshpaliyal.keypass.ui.redux.actions.UpdateContextAction
import com.yogeshpaliyal.keypass.ui.redux.actions.UpdateDialogAction
import com.yogeshpaliyal.keypass.ui.redux.actions.UpdateViewModalAction
import com.yogeshpaliyal.keypass.ui.redux.middlewares.intentNavigationMiddleware
import com.yogeshpaliyal.keypass.ui.redux.middlewares.utilityMiddleware
import com.yogeshpaliyal.keypass.ui.redux.states.BottomSheetState
import com.yogeshpaliyal.keypass.ui.redux.states.KeyPassState
import com.yogeshpaliyal.keypass.ui.redux.states.ScreenState
import com.yogeshpaliyal.keypass.ui.redux.states.generateDefaultState
import org.reduxkotlin.Reducer
import org.reduxkotlin.applyMiddleware
import org.reduxkotlin.createStore

object KeyPassRedux {

    private var arrPages = mutableListOf<ScreenState>()

    fun getLastScreen() = arrPages.lastOrNull()

    private val reducer: Reducer<KeyPassState> = { state, action ->
        if (action is BatchActions) {
            action.actions.fold(state) { acc, act ->
                handleAction(act, acc)
            }
        } else {
            handleAction(action, state)
        }
    }

    private fun handleAction(action: Any, state: KeyPassState): KeyPassState {
        return when (action) {
            is NavigationAction -> {
                if (action.clearBackStack) {
                    arrPages.clear()
                } else {
                    arrPages.add(state.currentScreen)
                }
                if (state.currentScreen == action.state) {
                    arrPages.remove(state.currentScreen)
                }
                state.copy(currentScreen = action.state)
            }

            is StateUpdateAction -> {
                state.copy(currentScreen = action.state)
            }

            is UpdateContextAction -> {
                state.copy(context = action.context)
            }

            is GoBackAction -> {
                val lastItem = arrPages.removeLastOrNull()
                if (lastItem != null) {
                    state.copy(currentScreen = lastItem)
                } else {
                    state.copy(systemBackPress = true)
                }
            }

            is BottomSheetAction -> {
                state.copy(
                    bottomSheet = BottomSheetState(
                        action.route,
                        action.globalArgs,
                        action.globalIsBottomSheetOpen
                    )
                )
            }

            is UpdateDialogAction -> {
                state.copy(dialog = action.dialogState)
            }

            is UpdateViewModalAction -> {
                state.copy(viewModel = action.viewModal)
            }

            is RestoreAccountsAction -> {
                state.viewModel?.restoreBackup(action.accounts)
                state
            }

            else -> state
        }
    }

    fun createStore() =
        createStore(
            reducer,
            generateDefaultState(),
            applyMiddleware(utilityMiddleware, intentNavigationMiddleware)
        )
}
