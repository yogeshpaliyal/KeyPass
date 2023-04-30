package com.yogeshpaliyal.keypass.ui.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.keypass.ui.home.SortingField
import com.yogeshpaliyal.keypass.ui.home.SortingOrder

@Composable
fun SearchBar(
    keyword: String?,
    updateKeyword: (keyword: String) -> Unit,
    updateSorting: (SortingField, SortingOrder) -> Unit
) {
    val (isMenuVisible, setMenuVisible) = rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(16.dp),
        value = keyword ?: "",
        placeholder = {
            Text(text = "Search Account")
        },
        onValueChange = { newValue ->
            updateKeyword(newValue)
        },
        trailingIcon = {
            Row {
                AnimatedVisibility(keyword.isNullOrBlank().not()) {
                    IconButton(onClick = { updateKeyword("") }) {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Rounded.Close),
                            contentDescription = ""
                        )
                    }
                }

                IconButton(onClick = {
                    setMenuVisible(!isMenuVisible)
                }) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Rounded.Sort),
                        contentDescription = ""
                    )
                }
            }

            SortingMenu(isMenuVisible, setMenuVisible) { sortingField, order ->
                updateSorting(sortingField, order)
                setMenuVisible(false)
            }
        }
    )
}
