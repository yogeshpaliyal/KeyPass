package com.yogeshpaliyal.keypass.ui.backupsImport

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.importer.ChromeAccountImporter
import com.yogeshpaliyal.keypass.importer.KeyPassAccountImporter
import com.yogeshpaliyal.keypass.ui.commonComponents.DefaultBottomAppBar
import com.yogeshpaliyal.keypass.ui.home.DashboardViewModel
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.StateUpdateAction
import com.yogeshpaliyal.keypass.ui.redux.states.BackupImporterState
import com.yogeshpaliyal.keypass.ui.settings.OpenKeyPassBackup
import com.yogeshpaliyal.keypass.ui.settings.PreferenceItem
import org.reduxkotlin.compose.rememberTypedDispatcher

val listOfBackupItems = listOf(KeyPassAccountImporter(), ChromeAccountImporter())

@Composable
fun BackupImporter(state: BackupImporterState, mViewModel: DashboardViewModel = hiltViewModel()) {
    val (result, setResult) = remember { mutableStateOf<Uri?>(null) }

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

    LaunchedEffect(key1 = restoredAccounts, block = {
        if (restoredAccounts?.isNotEmpty() == true) {
            mViewModel.restoreBackup(restoredAccounts)
            setRestoredAccounts(null)
        }
    })

    LaunchedEffect(key1 = state.selectedImported, block = {
        state.selectedImported?.let {
            launcher.launch(arrayOf())
        }
    })

    result?.let {
        state.selectedImported?.readFile(result, {
            setResult(null)
            setRestoredAccounts(it)
        }) {
            it?.let(dispatchAction)
            dispatchAction(StateUpdateAction(state = state.copy(selectedImported = null)))
        }
    }

    Scaffold(bottomBar = {
        DefaultBottomAppBar()
    }) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth(1f)
                .padding(16.dp)
        ) {
            item {
                PreferenceItem(title = R.string.restore_credentials, isCategory = true, removeIconSpace = true)
            }
            items(listOfBackupItems) {
                PreferenceItem(title = it.getImporterTitle(), summary = it.getImporterDesc(), removeIconSpace = true) {
                    dispatchAction(StateUpdateAction(state = state.copy(selectedImported = it)))
                }
            }
        }
    }
}
