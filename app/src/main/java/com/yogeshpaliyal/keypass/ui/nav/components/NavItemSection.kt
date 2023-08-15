package com.yogeshpaliyal.keypass.ui.nav.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.keypass.ui.nav.NavigationModelItem
import java.util.Locale

@Composable
fun NavItemSection(divider: NavigationModelItem.NavDivider) {
    Column(modifier = Modifier.padding(16.dp)) {
        Divider()
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = divider.title.uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.labelMedium,
            fontSize = TextUnit(12f, TextUnitType.Sp)
        )
    }
}
