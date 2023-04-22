package com.yogeshpaliyal.keypass.ui.redux

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.addTOTP.AddTOTPActivity
import com.yogeshpaliyal.keypass.ui.detail.DetailActivity
import com.yogeshpaliyal.keypass.ui.generate.GeneratePasswordActivity
import org.reduxkotlin.Reducer
import org.reduxkotlin.applyMiddleware
import org.reduxkotlin.createStore
import org.reduxkotlin.middleware

object KeyPassRedux {

    private fun NavController.navigateIfNotInSameRoute(
        route: String,
        builder: (NavOptionsBuilder.() -> Unit)? = null
    ) {
        if (this.currentDestination?.route != route) {
            if (builder != null) {
                this.navigate(route, builder)
            } else {
                this.navigate(route)
            }
        }
    }

    private val reducer: Reducer<KeyPassState> = { state, action ->
        when (action) {
            is ScreeNavigationAction -> {
                state.copy(currentScreen = ScreenState(action.route, action.globalArgs))
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

            is UpdateNavControllerAction -> {
                state.copy(navController = action.navController)
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

    val navigationMiddleware = middleware<KeyPassState> { store, next, action ->
        val state = store.state

        when (action) {
            is ScreeNavigationAction.GeneratePassword -> {
                val intent = Intent(state.context, GeneratePasswordActivity::class.java)
                state.context?.startActivity(intent)
            }

            is ScreeNavigationAction.AddTOTP -> {
                AddTOTPActivity.start(state.context, action.accountId)
            }

            is ScreeNavigationAction.AddAccount -> {
                DetailActivity.start(state.context, action.accountId)
            }

            is ScreeNavigationAction.Home -> {
                state.navController?.navigateIfNotInSameRoute(action.route) {
                    launchSingleTop = true
                }
            }

            is ScreeNavigationAction.Settings -> {
                state.navController?.navigateIfNotInSameRoute(action.route)
            }
        }
        next(action)
    }

    val store = createStore(reducer, generateDefaultState(), applyMiddleware(navigationMiddleware))
}
