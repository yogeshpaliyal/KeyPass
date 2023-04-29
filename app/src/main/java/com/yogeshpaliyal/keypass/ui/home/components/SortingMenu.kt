package com.yogeshpaliyal.keypass.ui.home.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.yogeshpaliyal.keypass.ui.home.SortingField
import com.yogeshpaliyal.keypass.ui.home.SortingOrder
import me.saket.cascade.CascadeDropdownMenu
import me.saket.cascade.rememberCascadeState

@Composable
fun SortingMenu(
    isMenuVisible: Boolean,
    setMenuVisible: (Boolean) -> Unit,
    onOptionSelected: (SortingField, SortingOrder) -> Unit
) {
    val state = rememberCascadeState()

    val sortingOptions =
        remember { mutableListOf(SortingField.Title, SortingField.Username) }

    CascadeDropdownMenu(
        state = state,
        expanded = isMenuVisible,
        onDismissRequest = { setMenuVisible(false) }
    ) {
        sortingOptions.forEach { sortingField ->
            DropdownMenuItem(
                text = { Text(stringResource(id = sortingField.label)) },
                children = {
                    sortingField.sortingOrders.forEach {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = it.label)) },
                            onClick = {
                                onOptionSelected(sortingField, it)
                            }
                        )
                    }
                }
            )
        }
    }
}
