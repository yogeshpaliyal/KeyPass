package com.yogeshpaliyal.keypass.ui.nav

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DiffUtil
import com.yogeshpaliyal.keypass.data.StringDiffUtil

/**
 * A sealed class which encapsulates all objects [NavigationAdapter] is able to display.
 */
sealed class NavigationModelItem {

    /**
     * A class which represents a checkable, navigation destination such as 'Inbox' or 'Sent'.
     */
    data class NavMenuItem(
        val id: Int,
        @DrawableRes val icon: Int,
        @StringRes val titleRes: Int,
        var checked: Boolean
    ) : NavigationModelItem()

    /**
     * A class which is used to show a section divider (a subtitle and underline) between
     * sections of different NavigationModelItem types.
     */
    data class NavDivider(val title: String) : NavigationModelItem()

    /**
     * A class which is used to show an [EmailFolder] in the [NavigationAdapter].
     */
    data class NavEmailFolder(val category: String) : NavigationModelItem()

    object NavModelItemDiff : DiffUtil.ItemCallback<NavigationModelItem>() {
        override fun areItemsTheSame(
            oldItem: NavigationModelItem,
            newItem: NavigationModelItem
        ): Boolean {
            return when {
                oldItem is NavMenuItem && newItem is NavMenuItem ->
                    oldItem.id == newItem.id
                oldItem is NavEmailFolder && newItem is NavEmailFolder ->
                    StringDiffUtil.areItemsTheSame(oldItem.category, newItem.category)
                else -> oldItem == newItem
            }
        }

        override fun areContentsTheSame(
            oldItem: NavigationModelItem,
            newItem: NavigationModelItem
        ): Boolean {
            return when {
                oldItem is NavMenuItem && newItem is NavMenuItem ->
                    oldItem.icon == newItem.icon &&
                        oldItem.titleRes == newItem.titleRes &&
                        oldItem.checked == newItem.checked
                oldItem is NavEmailFolder && newItem is NavEmailFolder ->
                    StringDiffUtil.areContentsTheSame(oldItem.category, newItem.category)
                else -> false
            }
        }
    }
}
