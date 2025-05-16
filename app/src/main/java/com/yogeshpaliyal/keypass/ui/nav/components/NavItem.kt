package com.yogeshpaliyal.keypass.ui.nav.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.keypass.ui.nav.NavigationModelItem

@Composable
fun NavItem(item: NavigationModelItem.NavMenuItem, onClick: () -> Unit) {
    TextButton(onClick) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(1f)
        ) {
            Icon(
                painter = painterResource(id = item.icon),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(32.dp))
            Text(
                text = stringResource(id = item.titleRes),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }

}
