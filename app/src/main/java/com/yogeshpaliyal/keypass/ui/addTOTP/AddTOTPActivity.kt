package com.yogeshpaliyal.keypass.ui.addTOTP

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.utils.TOTPHelper
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.detail.DeleteConfirmation
import com.yogeshpaliyal.keypass.ui.detail.KeyPassInputField
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

@Composable
fun Fields(
    modifier: Modifier = Modifier,
    accountModel: AccountModel,
    updateAccountModel: (newAccountModel: AccountModel) -> Unit,
    scanClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        if (accountModel.uniqueId == null) {

            KeyPassInputField(modifier = Modifier.testTag("secretKey"),
                placeholder = R.string.secret_key,
                value = accountModel.password,
                setValue = {
                    updateAccountModel(accountModel.copy(password = it))
                },
                trailingIcon = {
                    IconButton(onClick = {
                        scanClicked()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_twotone_qr_code_scanner_24),
                            contentDescription = ""
                        )
                    }
                })
        }

        KeyPassInputField(modifier = Modifier.testTag("accountName"),
            placeholder = R.string.account_name,
            value = accountModel.title,
            setValue = {
                updateAccountModel(accountModel.copy(title = it))
            })
    }
}

@Composable
fun BottomBar(
    backPressed: () -> Unit,
    showDeleteButton: Boolean,
    onDeletePressed: () -> Unit,
    onSaveClicked: () -> Unit
) {


    val (openDialog, setOpenDialog) = remember { mutableStateOf(false) }


    BottomAppBar(actions = {
        IconButton(onClick = backPressed) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Rounded.ArrowBackIosNew),
                contentDescription = "Go Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        if (showDeleteButton) {
            IconButton(onClick = {
                setOpenDialog(true)
            }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.Delete),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }, floatingActionButton = {
        FloatingActionButton(modifier = Modifier.testTag("save"), onClick = onSaveClicked) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Rounded.Done),
                contentDescription = "Save Changes"
            )
        }
    })

    DeleteConfirmation(
        openDialog,
        updateDialogVisibility = {
            setOpenDialog(it)
        },
        onDeletePressed
    )
}
