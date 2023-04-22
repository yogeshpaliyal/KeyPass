package com.yogeshpaliyal.keypass.ui.redux

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

object ScreenRoutes {
    const val HOME = "home?tag={tag}"
    const val SETTINGS = "/settings"
    const val ADD_ACCOUNT = "/addAccount"
    const val GENERATE_PASSWORD = "/generatePassword"
    const val ADD_TOTP = "/addTOTP"
}

object Home {
    fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument("tag") {
                type = NavType.StringType
            },
            navArgument("query") {
                type = NavType.StringType
            }
        )
    }
}

object BottomSheetRoutes {
    const val HOME_NAV_MENU = "/home/navMenu"
}
