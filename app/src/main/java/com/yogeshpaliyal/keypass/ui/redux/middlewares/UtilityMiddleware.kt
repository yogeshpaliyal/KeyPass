package com.yogeshpaliyal.keypass.ui.redux.middlewares

import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.yogeshpaliyal.keypass.ui.redux.actions.BatchActions
import com.yogeshpaliyal.keypass.ui.redux.actions.CopyToClipboard
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastActionStr
import com.yogeshpaliyal.keypass.ui.redux.actions.UtilityAction
import com.yogeshpaliyal.keypass.ui.redux.states.KeyPassState
import org.reduxkotlin.Store
import org.reduxkotlin.middleware

/**
 * Middle ware to handle utility functions like copy to clipboard and toast
 */
val utilityMiddleware = middleware<KeyPassState> { store, next, action ->
    val state = store.state
    if (action is BatchActions) {
        action.actions.forEach {
            store.handleAction(it, state)
        }
    } else {
        store.handleAction(action, state)
    }
    next(action)
}

private fun Store<KeyPassState>.handleAction(action: Any, state: KeyPassState) {
    if (action is UtilityAction) {
        when (action) {
            is ToastActionStr -> {
                state.context?.let {
                    Toast.makeText(it, action.text, Toast.LENGTH_SHORT).show()
                }
            }

            is ToastAction -> {
                state.context?.let {
                    Toast.makeText(it, action.text, Toast.LENGTH_SHORT).show()
                }
            }

            is CopyToClipboard -> {
                state.context?.let {
                    val clipboard = ContextCompat.getSystemService(
                        it,
                        ClipboardManager::class.java
                    )
                    val clip = ClipData.newPlainText("KeyPass", action.password)
                    clipboard?.setPrimaryClip(clip)
                    dispatch(ToastAction(action.successMessage))
                }
            }
        }
    }
}
