package com.yogeshpaliyal.keypass.ui.commonComponents

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceItem(
    title: String? = null,
    summary: String? = null,
    icon: ImageVector? = null,
    painter: Painter? = null,
    isCategory: Boolean = false,
    removeIconSpace: Boolean = false,
    onClickItem: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(1f)
            .clickable(onClickItem != null, onClick = {
                onClickItem?.invoke()
            })
            .widthIn(48.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!removeIconSpace) {
            Box(modifier = Modifier.width(56.dp), Alignment.CenterStart) {
                if (painter != null) {
                    Icon(painter = painter, contentDescription = "")
                } else if (icon != null) {
                    Icon(painter = rememberVectorPainter(image = icon), contentDescription = "")
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(1f)
        ) {
            if (title != null) {
                if (isCategory) {
                    CategoryTitle(title = title)
                } else {
                    PreferenceItemTitle(title = title)
                }
            }
            if (summary != null) {
                Text(text = summary, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}


@Composable
fun PreferenceItem(
    @StringRes title: Int? = null,
    @StringRes summary: Int? = null,
    summaryStr: String? = null,
    icon: ImageVector? = null,
    painter: Painter? = null,
    isCategory: Boolean = false,
    removeIconSpace: Boolean = false,
    onClickItem: (() -> Unit)? = null
) {
    PreferenceItem(
        title?.let { stringResource(it) },
        summary?.let { stringResource(summary) } ?: summaryStr,
        icon,
        painter,
        isCategory,
        removeIconSpace,
        onClickItem
    )
}

@Composable
private fun CategoryTitle(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.tertiary,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
private fun PreferenceItemTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge
    )
}
