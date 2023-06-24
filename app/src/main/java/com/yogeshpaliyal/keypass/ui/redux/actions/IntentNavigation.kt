package com.yogeshpaliyal.keypass.ui.redux.actions

sealed interface IntentNavigation : Action {
    object GeneratePassword : IntentNavigation
    object BackupActivity : IntentNavigation
    object ShareApp : IntentNavigation
}
