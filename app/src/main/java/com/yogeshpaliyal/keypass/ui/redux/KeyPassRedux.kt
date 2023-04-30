package com.yogeshpaliyal.keypass.ui.redux

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.yogeshpaliyal.keypass.BuildConfig
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.addTOTP.AddTOTPActivity
import com.yogeshpaliyal.keypass.ui.backup.BackupActivity
import com.yogeshpaliyal.keypass.ui.generate.GeneratePasswordActivity
import org.reduxkotlin.Reducer
import org.reduxkotlin.applyMiddleware
import org.reduxkotlin.createStore
import org.reduxkotlin.middleware

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

    private val intentNavigationMiddleware = middleware<KeyPassState> { store, next, action ->
        val state = store.state

        when (action) {
            is IntentNavigation.GeneratePassword -> {
                val intent = Intent(state.context, GeneratePasswordActivity::class.java)
                state.context?.startActivity(intent)
            }

            is IntentNavigation.AddTOTP -> {
                AddTOTPActivity.start(state.context, action.accountId)
            }

            is IntentNavigation.BackupActivity -> {
                BackupActivity.start(state.context)
            }

            is IntentNavigation.ShareApp -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "KeyPass Password Manager\n Offline, Secure, Open Source https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )
                sendIntent.type = "text/plain"
                state.context?.startActivity(Intent.createChooser(sendIntent, state.context.getString(R.string.share_keypass)))
            }
        }
        next(action)
    }

    fun createStore() =
        createStore(reducer, generateDefaultState(), applyMiddleware(intentNavigationMiddleware))
}
