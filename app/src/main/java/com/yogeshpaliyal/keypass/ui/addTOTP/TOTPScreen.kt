package com.yogeshpaliyal.keypass.ui.addTOTP

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
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.utils.TOTPHelper
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.addTOTP.components.BottomBar
import com.yogeshpaliyal.keypass.ui.addTOTP.components.Fields
import com.yogeshpaliyal.keypass.ui.detail.QRScanner
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.GoBackAction
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import org.reduxkotlin.compose.rememberTypedDispatcher

@Composable
fun TOTPScreen(id: String? = null, viewModel: AddTOTPViewModel = hiltViewModel()) {
    val dispatchAction = rememberTypedDispatcher<Action>()

    // task value state
    val (accountModel, setAccountModel) = remember {
        mutableStateOf(
            AccountModel()
        )
    }

    val launcher = rememberLauncherForActivityResult(QRScanner()) {
        it?.let {
            val totp = TOTPHelper(it)
            setAccountModel(
                accountModel.copy(
                    password = totp.secret,
                    title = totp.label,
                    username = totp.issuer
                )
            )
        }
    }

    val goBack: () -> Unit = {
        dispatchAction(GoBackAction)
    }

    // Set initial object
    LaunchedEffect(key1 = Unit) {
        viewModel.loadOldAccount(id) {
            setAccountModel(it.copy())
        }
    }

    Scaffold(bottomBar = {
        BottomBar(
            backPressed = goBack,
            showDeleteButton = accountModel.uniqueId != null,
            onDeletePressed = {
                accountModel.uniqueId?.let {
                    viewModel.deleteAccount(it) {
                        goBack()
                    }
                }
            }
        ) {
            if (accountModel.password.isNullOrEmpty()) {
                dispatchAction(ToastAction(R.string.alert_black_secret_key))
                return@BottomBar
            }

            if (accountModel.title.isNullOrEmpty()) {
                dispatchAction(ToastAction(R.string.alert_black_account_name))
                return@BottomBar
            }

            viewModel.saveAccount(accountModel, goBack)
        }
    }) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            Fields(accountModel = accountModel, updateAccountModel = { newAccountModel ->
                setAccountModel(newAccountModel)
            }) {
                launcher.launch(null)
            }
        }
    }
}
