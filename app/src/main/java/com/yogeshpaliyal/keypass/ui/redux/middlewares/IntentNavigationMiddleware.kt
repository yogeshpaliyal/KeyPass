package com.yogeshpaliyal.keypass.ui.redux.middlewares

import android.content.Intent
import com.yogeshpaliyal.keypass.BuildConfig
import com.yogeshpaliyal.keypass.MyApplication
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.generate.GeneratePasswordActivity
import com.yogeshpaliyal.keypass.ui.redux.actions.BatchActions
import com.yogeshpaliyal.keypass.ui.redux.actions.IntentNavigation
import com.yogeshpaliyal.keypass.ui.redux.states.KeyPassState
import org.reduxkotlin.Store
import org.reduxkotlin.middleware

/**
 * Middleware to handle intent navigation
 */
val intentNavigationMiddleware = middleware<KeyPassState> { store, next, action ->
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
    when (action) {
        is IntentNavigation.GeneratePassword -> {
            (state.context?.applicationContext as? MyApplication)?.knownActivityLaunchTriggered()
            val intent = Intent(state.context, GeneratePasswordActivity::class.java)
            state.context?.startActivity(intent)
        }

        is IntentNavigation.ShareApp -> {
            (state.context?.applicationContext as? MyApplication)?.knownActivityLaunchTriggered()
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "KeyPass Password Manager\n Offline, Secure, Open Source https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
            )
            sendIntent.type = "text/plain"
            state.context?.startActivity(
                Intent.createChooser(
                    sendIntent,
                    state.context.getString(R.string.share_keypass)
                )
            )
        }
    }
}
