package com.yogeshpaliyal.keypass.ui.nav

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.yogeshpaliyal.keypass.ui.redux.Action

sealed class NavigationModelItem {

    data class NavMenuItem(
        val id: Int,
        @DrawableRes val icon: Int,
        @StringRes val titleRes: Int,
        var checked: Boolean,
        var action: Action
    ) : NavigationModelItem()

    /**
     * A class which is used to show a section divider (a subtitle and underline) between
     * sections of different NavigationModelItem types.
     */
    data class NavDivider(val title: String) : NavigationModelItem()

    data class NavTagItem(val tag: String) : NavigationModelItem()
}
