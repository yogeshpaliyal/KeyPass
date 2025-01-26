package com.yogeshpaliyal.keypass.ui.redux.actions

/**
 * Intent Navigation actions, used to navigate to different activities
 * handled in {@link IntentNavigationMiddleware}
 */
sealed interface IntentNavigation : Action {
    object GeneratePassword : IntentNavigation
    object ShareApp : IntentNavigation
}
