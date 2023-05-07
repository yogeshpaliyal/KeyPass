package com.yogeshpaliyal.keypass.ui.redux

import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.redux.actions.BottomSheetAction
import com.yogeshpaliyal.keypass.ui.redux.actions.CopyToClipboard
import com.yogeshpaliyal.keypass.ui.redux.actions.GoBackAction
import com.yogeshpaliyal.keypass.ui.redux.actions.NavigationAction
import com.yogeshpaliyal.keypass.ui.redux.actions.StateUpdateAction
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import com.yogeshpaliyal.keypass.ui.redux.actions.UpdateContextAction
import com.yogeshpaliyal.keypass.ui.redux.middlewares.intentNavigationMiddleware
import com.yogeshpaliyal.keypass.ui.redux.states.BottomSheetState
import com.yogeshpaliyal.keypass.ui.redux.states.KeyPassState
import com.yogeshpaliyal.keypass.ui.redux.states.ScreenState
import com.yogeshpaliyal.keypass.ui.redux.states.generateDefaultState
import org.reduxkotlin.Reducer
import org.reduxkotlin.applyMiddleware
import org.reduxkotlin.createStore

object KeyPassRedux {

    private var arrPages = mutableListOf<ScreenState>()

    private val reducer: Reducer<KeyPassState> = { state, action ->
        when (action) {
            is NavigationAction -> {
                if (action.clearBackStack) {
                    arrPages.clear()
                } else {
                    arrPages.add(state.currentScreen)
                }

                state.copy(currentScreen = action.state)
            }

            is StateUpdateAction -> {
                state.copy(currentScreen = action.state)
            }

            is CopyToClipboard -> {
                state.context?.let {
                    val clipboard = ContextCompat.getSystemService(
                        it,
                        ClipboardManager::class.java
                    )
                    val clip = ClipData.newPlainText("KeyPass", action.password)
                    clipboard?.setPrimaryClip(clip)

                    Toast.makeText(
                        it,
                        R.string.copied_to_clipboard,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                state
            }

            is UpdateContextAction -> {
                state.copy(context = action.context)
            }

            is ToastAction -> {
                state.context?.let {
                    Toast.makeText(it, action.text, Toast.LENGTH_SHORT).show()
                }
                state
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

            else -> state
        }
    }

    fun createStore() =
        createStore(
            reducer,
            generateDefaultState(),
            applyMiddleware(intentNavigationMiddleware)
        )
}
