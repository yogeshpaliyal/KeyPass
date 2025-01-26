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
import com.yogeshpaliyal.keypass.ui.redux.states.RestoreChromeBackupState
import org.reduxkotlin.compose.rememberTypedDispatcher
import java.io.FileNotFoundException

@Composable
fun RestoreChromeBackupDialog(
    state: RestoreChromeBackupState
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
                        title = it["name"],
                        notes = it["note"],
                        password = it["password"],
                        username = it["username"],
                        site = it["url"]
                    )
                )
            }
            dispatchAction(RestoreAccountsAction(listOfAccounts))
            onCompleteOrCancel(ToastAction(R.string.backup_restored))
        } catch (e: CsvMalformedLineException) {
            onCompleteOrCancel(ToastAction(R.string.invalid_csv_file))
        } catch (e: FileNotFoundException) {
            onCompleteOrCancel(ToastAction(R.string.file_not_found))
        }
    })

    LoadingDialog(R.string.restore)
}
