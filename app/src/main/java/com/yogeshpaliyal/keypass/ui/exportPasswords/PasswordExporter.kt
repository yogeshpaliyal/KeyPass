package com.yogeshpaliyal.keypass.ui.exportPasswords

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.exporter.BitwardenAccountsExporter
import com.yogeshpaliyal.keypass.ui.commonComponents.DefaultBottomAppBar
import com.yogeshpaliyal.keypass.ui.commonComponents.PreferenceItem
import com.yogeshpaliyal.keypass.ui.home.DashboardViewModel
import com.yogeshpaliyal.keypass.ui.redux.states.ExportPasswordState
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter

val listOfExporters = listOf(BitwardenAccountsExporter())

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PasswordExporter(state: ExportPasswordState, mViewModel: DashboardViewModel = hiltViewModel()) {
    var fileText : String = ""
    val context = LocalContext.current
    val listOfAccountsLiveData by mViewModel.mediator.observeAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain"),
        onResult = {
            val outputStream : OutputStream? = context.contentResolver.openOutputStream(it!!)
            val bufferedWriter : BufferedWriter = BufferedWriter(OutputStreamWriter(outputStream))
            bufferedWriter.write(fileText)
            bufferedWriter.flush()
            bufferedWriter.close()
        }
    )

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
                PreferenceItem(
                    title = R.string.export_passwords_title,
                    isCategory = true,
                    removeIconSpace = true
                )
            }
            items(listOfExporters) {
                PreferenceItem(
                    title = it.getExporterTitle(),
                    summary = it.getExporterDesc(),
                    removeIconSpace = true
                ) {
                    val exportedFile = it.export(context, listOfAccountsLiveData!!.toList())
                    fileText = exportedFile!!.readText()
                    launcher.launch(exportedFile!!.name)
                }
            }
        }
    }
}