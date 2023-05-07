package com.yogeshpaliyal.keypass.ui.home

import androidx.annotation.StringRes
import com.yogeshpaliyal.keypass.R

sealed class SortingOrder(@StringRes val label: Int) {
    object Ascending : SortingOrder(R.string.ascending)
    object Descending : SortingOrder(R.string.descending)
}

fun getSortingOrderOptions() = mutableListOf(SortingOrder.Ascending, SortingOrder.Descending)

sealed class SortingField(
    @StringRes val label: Int,
    val value: String,
    val sortingOrders: List<SortingOrder>
) {
    object Title : SortingField(R.string.account_name, "title", getSortingOrderOptions())
    object Username : SortingField(R.string.username_email_phone, "username", getSortingOrderOptions())
}
