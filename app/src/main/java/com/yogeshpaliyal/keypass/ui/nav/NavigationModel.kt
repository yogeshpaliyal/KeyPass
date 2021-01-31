package com.yogeshpaliyal.keypass.ui.nav

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yogeshpaliyal.keypass.R

/**
 * A class which maintains and generates a navigation list to be displayed by [NavigationAdapter].
 */
object NavigationModel {

    const val HOME = 0
    const val GENERATE_PASSWORD = 1


    var navigationMenuItems = mutableListOf(
        NavigationModelItem.NavMenuItem(
            id = HOME,
            icon = R.drawable.ic_twotone_home_24,
            titleRes = R.string.home,
            checked = false,
        ),
        NavigationModelItem.NavMenuItem(
            id = GENERATE_PASSWORD,
            icon = R.drawable.ic_twotone_vpn_key_24,
            titleRes = R.string.generate_password,
            checked = false,
        )
    )






}

