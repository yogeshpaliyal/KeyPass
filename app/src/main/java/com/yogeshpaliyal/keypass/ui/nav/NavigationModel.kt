package com.yogeshpaliyal.keypass.ui.nav

import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.redux.HomeState
import com.yogeshpaliyal.keypass.ui.redux.IntentNavigation
import com.yogeshpaliyal.keypass.ui.redux.NavigationAction

object NavigationModel {

    const val HOME = 0
    const val GENERATE_PASSWORD = 1
    const val ADD_TOPT = 2

    var navigationMenuItems = mutableListOf(
        NavigationModelItem.NavMenuItem(
            id = HOME,
            icon = R.drawable.ic_twotone_home_24,
            titleRes = R.string.home,
            checked = false,
            action = NavigationAction(HomeState(), true)
        ),
        NavigationModelItem.NavMenuItem(
            id = GENERATE_PASSWORD,
            icon = R.drawable.ic_twotone_vpn_key_24,
            titleRes = R.string.generate_password,
            checked = false,
            action = IntentNavigation.GeneratePassword
        ),
        NavigationModelItem.NavMenuItem(
            id = ADD_TOPT,
            icon = R.drawable.ic_twotone_totp,
            titleRes = R.string.add_totp,
            checked = false,
            action = IntentNavigation.AddTOTP()
        )
    )
}
