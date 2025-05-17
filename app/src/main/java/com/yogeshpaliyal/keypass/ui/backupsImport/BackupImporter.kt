package com.yogeshpaliyal.keypass.ui.backupsImport

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.importer.AccountsImporter
import com.yogeshpaliyal.keypass.importer.ChromeAccountImporter
import com.yogeshpaliyal.keypass.importer.KeePassAccountImporter
import com.yogeshpaliyal.keypass.importer.KeyPassAccountImporter
import com.yogeshpaliyal.keypass.ui.commonComponents.DefaultBottomAppBar
import com.yogeshpaliyal.keypass.ui.commonComponents.DefaultTopAppBar
import com.yogeshpaliyal.keypass.ui.home.DashboardViewModel
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.StateUpdateAction
import com.yogeshpaliyal.keypass.ui.redux.states.BackupImporterState
import com.yogeshpaliyal.keypass.ui.settings.OpenKeyPassBackup
import org.reduxkotlin.compose.rememberTypedDispatcher

// Define import option data structure
data class ImportOption(
    val importer: AccountsImporter,
    val icon: ImageVector,
    val backgroundColor: androidx.compose.ui.graphics.Color
)

// Create list of import options with metadata
@Composable
private fun getImportOptions(): List<ImportOption> {
    return listOf(
        ImportOption(
            importer = KeyPassAccountImporter(),
            icon = Icons.Filled.Key,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer
        ),
        ImportOption(
            importer = ChromeAccountImporter(),
            icon = Icons.Filled.Sync,
            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        ImportOption(
            importer = KeePassAccountImporter(),
            icon = Icons.Filled.DataArray,
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer
        )
    )
}

@Composable
fun BackupImporter(state: BackupImporterState, mViewModel: DashboardViewModel = hiltViewModel()) {
    val (result, setResult) = remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val dispatchAction = rememberTypedDispatcher<Action>()

    val (restoredAccounts, setRestoredAccounts) = remember {
        mutableStateOf<List<AccountModel>?>(
            listOf()
        )
    }

    val launcher =
        rememberLauncherForActivityResult(OpenKeyPassBackup(state.selectedImported)) {
            setResult(it)
            if (it == null) {
                dispatchAction(StateUpdateAction(state.copy(selectedImported = null)))
            }
        }

    // Get import options list with icons and colors
    val importOptions = getImportOptions()

    LaunchedEffect(key1 = restoredAccounts, block = {
        if (restoredAccounts?.isNotEmpty() == true) {
            isLoading = true
            mViewModel.restoreBackup(restoredAccounts)
            isLoading = false
            setRestoredAccounts(null)
        }
    })

    LaunchedEffect(key1 = state.selectedImported, block = {
        state.selectedImported?.let {
            isLoading = true
            launcher.launch(arrayOf())
        }
    })

    result?.let {
        state.selectedImported?.readFileGetAction(result)?.let { it1 ->
            isLoading = false
            dispatchAction(it1)
        }
    }

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                showBackButton = true,
                title = R.string.restore_credentials,
                subtitle = R.string.restore_credentials_desc
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {

                            // Info card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Text(
                                    text = "Select a backup format below to import your credentials",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    items(importOptions) { option ->
                        ImportOptionCard(
                            option = option,
                            onClick = {
                                dispatchAction(StateUpdateAction(state = state.copy(selectedImported = option.importer)))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImportOptionCard(option: ImportOption, onClick: () -> Unit) {
    val desc = option.importer.getImporterDesc()
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(option.backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(id = option.importer.getImporterTitle()),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = if (desc == null) "" else stringResource(id = desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Filled.FileUpload,
                contentDescription = "Import",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
