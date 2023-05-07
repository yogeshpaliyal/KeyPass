package com.yogeshpaliyal.keypass.ui.detail

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.utils.PasswordGenerator
import com.yogeshpaliyal.keypass.R
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
    id: Long?,
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
    LaunchedEffect(key1 = id) {
        viewModel.loadAccount(id) {
            setAccountModel(it.copy())
        }
    }

    val goBack: () -> Unit = {
        dispatchAction(GoBackAction)
    }

    val launcher = rememberLauncherForActivityResult(QRScanner()) {
        it?.let {
            setAccountModel(accountModel.copy(password = it))
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
                }
            ) {
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
        KeyPassInputField(
            modifier = Modifier.testTag("accountName"),
            placeholder = R.string.account_name,
            value = accountModel.title,
            setValue = {
                updateAccountModel(accountModel.copy(title = it))
            }
        )

        KeyPassInputField(
            modifier = Modifier.testTag("username"),
            placeholder = R.string.username_email_phone,
            value = accountModel.username,
            setValue = {
                updateAccountModel(accountModel.copy(username = it))
            }
        )

        Column {
            val passwordVisible = rememberSaveable { mutableStateOf(false) }

            val visualTransformation =
                if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation()

            KeyPassInputField(
                modifier = Modifier.testTag("password"),
                placeholder = R.string.password,
                value = accountModel.password,
                setValue = {
                    updateAccountModel(accountModel.copy(password = it))
                },
                trailingIcon = {
                    PasswordTrailingIcon(passwordVisible.value) {
                        passwordVisible.value = it
                    }
                },
                leadingIcon = if (accountModel.id != null) {
                    null
                } else (
                    {
                        IconButton(
                            onClick = {
                                updateAccountModel(accountModel.copy(password = PasswordGenerator().generatePassword()))
                            }
                        ) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Rounded.Refresh),
                                contentDescription = ""
                            )
                        }
                    }
                    ),
                visualTransformation = visualTransformation
            )
            Button(onClick = scanClicked) {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_twotone_qr_code_scanner_24),
                        contentDescription = ""
                    )
                    Text(text = stringResource(id = R.string.scan_password))
                }
            }
        }

        KeyPassInputField(
            modifier = Modifier.testTag("tags"),
            placeholder = R.string.tags_comma_separated_optional,
            value = accountModel.tags,
            setValue = {
                updateAccountModel(accountModel.copy(tags = it))
            }
        )

        KeyPassInputField(
            modifier = Modifier.testTag("website"),
            placeholder = R.string.website_url_optional,
            value = accountModel.site,
            setValue = {
                updateAccountModel(accountModel.copy(site = it))
            }
        )

        KeyPassInputField(
            modifier = Modifier.testTag("notes"),
            placeholder = R.string.notes_optional,
            value = accountModel.notes,
            setValue = {
                updateAccountModel(accountModel.copy(notes = it))
            }
        )
    }
}

@Composable
fun DeleteConfirmation(
    openDialog: Boolean,
    updateDialogVisibility: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = { /*TODO*/ },
            title = {
                Text(text = stringResource(id = R.string.delete_account_title))
            },
            confirmButton = {
                TextButton(
                    modifier = Modifier.testTag("delete"),
                    onClick = {
                        updateDialogVisibility(false)
                        onDelete()
                    }
                ) {
                    Text(text = stringResource(id = R.string.delete))
                }
            },
            text = {
                Text(text = stringResource(id = R.string.delete_account_msg))
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        updateDialogVisibility(false)
                    }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun KeyPassInputField(
    modifier: Modifier = Modifier,
    @StringRes placeholder: Int,
    value: String?,
    setValue: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value ?: "",
        label = {
            Text(text = stringResource(id = placeholder))
        },
        onValueChange = setValue,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation
    )
}

@Composable
fun PasswordTrailingIcon(
    passwordVisible: Boolean,
    changePasswordVisibility: (updatedValue: Boolean) -> Unit
) {
    val description = if (passwordVisible) "Hide password" else "Show password"

    val image = if (passwordVisible) {
        Icons.Rounded.Visibility
    } else {
        Icons.Rounded.VisibilityOff
    }

    IconButton(onClick = { changePasswordVisibility(!passwordVisible) }) {
        Icon(
            painter = rememberVectorPainter(image = image),
            contentDescription = description
        )
    }
}

@Composable
fun BottomBar(
    accountModel: AccountModel,
    backPressed: () -> Unit,
    onDeleteAccount: () -> Unit,
    onSaveClicked: () -> Unit
) {
    val openDialog = remember { mutableStateOf(false) }

    BottomAppBar(
        actions = {
            IconButton(onClick = backPressed) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.ArrowBackIosNew),
                    contentDescription = "Go Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            if (accountModel.id != null) {
                IconButton(
                    modifier = Modifier.testTag("action_delete"),
                    onClick = { openDialog.value = true }
                ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Rounded.Delete),
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(modifier = Modifier.testTag("save"), onClick = onSaveClicked) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.Done),
                    contentDescription = "Save Changes"
                )
            }
        }
    )

    DeleteConfirmation(
        openDialog.value,
        updateDialogVisibility = {
            openDialog.value = it
        },
        onDeleteAccount
    )
}
