package com.yogeshpaliyal.keypass.ui.redux.states

data class HomeState(
    val keyword: String? = null,
    val tag: String? = null,
    val sortField: String? = null,
    val sortAscending: Boolean = true
) : ScreenState(true)
