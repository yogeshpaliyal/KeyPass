package com.yogeshpaliyal.keypass.ui.redux.middlewares

import com.yogeshpaliyal.keypass.ui.redux.actions.BackupAction
import com.yogeshpaliyal.keypass.ui.redux.states.KeyPassState
import org.reduxkotlin.middleware

val backupMiddleware = middleware<KeyPassState> { store, next, action ->
    if (action is BackupAction) {

    }

    next(action)
}