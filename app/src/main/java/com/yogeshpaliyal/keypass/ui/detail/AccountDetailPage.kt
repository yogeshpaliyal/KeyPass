package com.yogeshpaliyal.keypass.ui.detail

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.yogeshpaliyal.common.constants.ScannerType
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.utils.TOTPHelper
import com.yogeshpaliyal.keypass.ui.detail.components.BottomBar
import com.yogeshpaliyal.keypass.ui.detail.components.Fields
import com.yogeshpaliyal.keypass.ui.redux.actions.CopyToClipboard
import com.yogeshpaliyal.keypass.ui.redux.actions.GoBackAction
import org.reduxkotlin.compose.rememberDispatcher

/*
* @author Yogesh Paliyal
* yogeshpaliyal.foss@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 10:38
*/

@Composable
fun AccountDetailPage(
    uniqueId: String?,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val dispatchAction = rememberDispatcher()

    // task value state
    val (accountModel, setAccountModel) = remember {
        mutableStateOf(
            AccountModel()
        )
    }

    // Set initial object
    LaunchedEffect(key1 = uniqueId) {
        viewModel.loadAccount(uniqueId) {
            setAccountModel(it.copy())
        }
    }

    val goBack: () -> Unit = {
        dispatchAction(GoBackAction)
    }

    val launcher = rememberLauncherForActivityResult(QRScanner()) {
        when (it.type) {
            ScannerType.Password -> {
                setAccountModel(accountModel.copy(password = it.scannedText))
            }
            ScannerType.Secret -> {
                it.scannedText ?: return@rememberLauncherForActivityResult
                val totp = TOTPHelper(it.scannedText)
                var newAccountModel = accountModel.copy(secret = totp.secret)

                if (newAccountModel.title.isNullOrEmpty()) {
                    newAccountModel = newAccountModel.copy(title = totp.label)
                }

                if (newAccountModel.username.isNullOrEmpty()) {
                    newAccountModel = newAccountModel.copy(username = totp.issuer)
                }

                setAccountModel(newAccountModel)
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomBar(
                accountModel,
                backPressed = goBack,
                onDeleteAccount = {
                    viewModel.deleteAccount(accountModel, goBack)
                }
            ) {
                viewModel.insertOrUpdate(accountModel, goBack)
            }
        }
    ) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            Fields(
                accountModel = accountModel,
                updateAccountModel = { newAccountModel ->
                    setAccountModel(newAccountModel)
                },
                copyToClipboardClicked = { value ->
                    dispatchAction(CopyToClipboard(value))
                }
            ) {
                launcher.launch(it)
            }
        }
    }
}
