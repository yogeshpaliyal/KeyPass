package com.yogeshpaliyal.keypass.importer

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.opencsv.CSVReader
import com.opencsv.exceptions.CsvMalformedLineException
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import java.io.FileNotFoundException

class ChromeAccountImporter : AccountsImporter {
    override fun getImporterTitle(): Int = R.string.google_backup
    override fun getImporterDesc(): Int? = null

    override fun allowedMimeType(): String {
        return "text/comma-separated-values"
    }

    @Composable
    override fun readFile(file: Uri, resolve: (List<AccountModel>) -> Unit, onCompleteOrCancel: (action: Action?) -> Unit) {
        val context = LocalContext.current

        LaunchedEffect(key1 = file, block = {
            try {
                val inputStream = context.contentResolver.openInputStream(file)
                val reader = CSVReader(inputStream?.reader())
                val myEntries: List<Array<String>> = reader.readAll()
                val headers = myEntries[0]
                val result = myEntries.drop(1).map { data ->
                    headers.zip(data).toMap()
                }
                val listOfAccounts = ArrayList<AccountModel>()
                result.forEach {
                    listOfAccounts.add(
                        AccountModel(
                            title = it["name"],
                            notes = it["note"],
                            password = it["password"],
                            username = it["username"],
                            site = it["url"]
                        )
                    )
                }
                resolve(listOfAccounts)
                onCompleteOrCancel(ToastAction(R.string.backup_restored))
            } catch (e: CsvMalformedLineException) {
                onCompleteOrCancel(ToastAction(R.string.invalid_csv_file))
            }  catch (e: FileNotFoundException) {
                onCompleteOrCancel(ToastAction(R.string.file_not_found))
            }
        })

        LoadingDialog()
    }
}

@Composable
fun LoadingDialog() {
    Dialog(onDismissRequest = {}) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
                .fillMaxWidth(1f)
                .padding(16.dp),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.restore),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.size(16.dp))
            CircularProgressIndicator()
        }
    }
}

@Preview()
@Composable
fun DialogPreview() {
    LoadingDialog()
}
