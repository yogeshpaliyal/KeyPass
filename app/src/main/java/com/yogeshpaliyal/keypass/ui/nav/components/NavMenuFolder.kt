package com.yogeshpaliyal.keypass.ui.nav.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.keypass.ui.nav.NavigationModelItem

@Composable
fun NavMenuFolder(folder: NavigationModelItem.NavTagItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true
                ),
                onClick = onClick
            )
            .padding(16.dp)
            .fillMaxWidth(1f)

    ) {
        Text(
            text = folder.tag,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
