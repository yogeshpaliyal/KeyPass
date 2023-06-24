package com.yogeshpaliyal.keypass.ui.redux.middlewares

import android.content.Intent
import com.yogeshpaliyal.keypass.BuildConfig
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.generate.GeneratePasswordActivity
import com.yogeshpaliyal.keypass.ui.redux.actions.IntentNavigation
import com.yogeshpaliyal.keypass.ui.redux.states.KeyPassState
import org.reduxkotlin.middleware

val intentNavigationMiddleware = middleware<KeyPassState> { store, next, action ->
    val state = store.state

    when (action) {
        is IntentNavigation.GeneratePassword -> {
            val intent = Intent(state.context, GeneratePasswordActivity::class.java)
            state.context?.startActivity(intent)
        }

        is IntentNavigation.BackupActivity -> {
            // BackupActivity.start(state.context)
        }

        is IntentNavigation.ShareApp -> {
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
    next(action)
}
