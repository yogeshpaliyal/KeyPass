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
import com.yogeshpaliyal.common.constants.AccountType
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction

class KeePassAccountImporter : AccountsImporter {
    override fun getImporterTitle(): Int = R.string.keepass_backup
    override fun getImporterDesc(): Int? = null

    override fun allowedMimeType(): String {
        return "text/comma-separated-values"
    }

    @Composable
    override fun readFile(file: Uri, resolve: (List<AccountModel>) -> Unit, onCompleteOrCancel: (action: Action?) -> Unit) {
        val context = LocalContext.current

        LaunchedEffect(key1 = file, block = {
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
                        title = it["Title"],
                        notes = it["Notes"],
                        password = it["Password"],
                        username = it["Username"],
                        site = it["URL"],
                        tags = it["Group"],
                        secret = if(it["TOTP"].isNullOrBlank()) null else it["TOTP"],
                    )
                )
            }
            resolve(listOfAccounts)
            onCompleteOrCancel(ToastAction(R.string.backup_restored))
        })

        LoadingDialog()
    }
}
