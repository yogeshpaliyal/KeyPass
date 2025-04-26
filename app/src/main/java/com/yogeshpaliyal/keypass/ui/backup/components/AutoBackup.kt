package com.yogeshpaliyal.keypass.ui.backup.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.commonComponents.PreferenceItem

@Composable
fun AutoBackup(
    isAutoBackupEnabled: Boolean?,
    overrideAutoBackup: Boolean?,
    setAutoBackupEnabled: (Boolean) -> Unit,
    setOverrideAutoBackup: (Boolean) -> Unit
) {
    PreferenceItem(
        title = R.string.auto_backup,
        removeIconSpace = true,
        summary = if (isAutoBackupEnabled == true) R.string.enabled else R.string.disabled
    ) {
        setAutoBackupEnabled(!(isAutoBackupEnabled ?: false))
    }

    AnimatedVisibility(visible = isAutoBackupEnabled == true) {
        Column {
            PreferenceItem(title = R.string.auto_backup, isCategory = true, removeIconSpace = true,)
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )
            PreferenceItem(summary = R.string.auto_backup_desc, removeIconSpace = true,)
            PreferenceItem(
                title = R.string.override_auto_backup_file,
                removeIconSpace = true,
                summary = if (overrideAutoBackup == true) R.string.enabled else R.string.disabled
            ) {
                setOverrideAutoBackup(!(overrideAutoBackup ?: false))
            }
        }
    }
}
