package com.yogeshpaliyal.keypass.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.opencsv.CSVReader
import com.opencsv.exceptions.CsvMalformedLineException
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.commonComponents.LoadingDialog
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.RestoreAccountsAction
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import com.yogeshpaliyal.keypass.ui.redux.actions.UpdateDialogAction
import com.yogeshpaliyal.keypass.ui.redux.states.RestoreKeePassBackupState
import org.reduxkotlin.compose.rememberTypedDispatcher

@Composable
fun RestoreKeePassBackupDialog(
    state: RestoreKeePassBackupState
) {
    val (selectedFile) = state

    val dispatchAction = rememberTypedDispatcher<Action>()

    val context = LocalContext.current

    val onCompleteOrCancel: (ToastAction) -> Unit = {
        dispatchAction(UpdateDialogAction(null))
        dispatchAction(it)
    }

    LaunchedEffect(key1 = selectedFile, block = {
        try {
            val inputStream = context.contentResolver.openInputStream(selectedFile)
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
                        secret = if (it["TOTP"].isNullOrBlank()) null else it["TOTP"]
                    )
                )
            }
            dispatchAction(RestoreAccountsAction(listOfAccounts))
            onCompleteOrCancel(ToastAction(R.string.backup_restored))
        } catch (e: CsvMalformedLineException) {
            onCompleteOrCancel(ToastAction(R.string.invalid_csv))
        }
    })

    LoadingDialog(R.string.restore)
}
