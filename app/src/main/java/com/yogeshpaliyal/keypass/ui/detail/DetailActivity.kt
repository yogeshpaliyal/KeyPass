package com.yogeshpaliyal.keypass.ui.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.utils.PasswordGenerator
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.style.KeyPassTheme
import dagger.hilt.android.AndroidEntryPoint

/*
* @author Yogesh Paliyal
* yogeshpaliyal.foss@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 10:38
*/
@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    companion object {

        private const val ARG_ACCOUNT_ID = "ARG_ACCOUNT_ID"

        @JvmStatic
        fun start(context: Context?, accountId: Long? = null) {
            val starter = Intent(context, DetailActivity::class.java)
                .putExtra(ARG_ACCOUNT_ID, accountId)
            context?.startActivity(starter)
        }
    }

    private val mViewModel by viewModels<DetailViewModel>()

    private val accountId by lazy {
        intent?.extras?.getLong(ARG_ACCOUNT_ID) ?: -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Detail(accountId, mViewModel)
        }

        mViewModel.loadAccount(accountId)
    }
}

@Preview()
@Composable
fun Detail(
    id: Long?,
    viewModel: DetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    // task value state
    val (accountModel, setAccountModel) = remember {
        mutableStateOf(
            AccountModel()
        )
    }

    // Set initial object
    LaunchedEffect(key1 = viewModel.accountModel) {
        viewModel.accountModel.value?.apply {
            setAccountModel(this.copy())
        }
    }

    val activity = (LocalContext.current as? Activity)
    val goBack: () -> Unit = {
        activity?.onBackPressed()
    }

    val launcher = rememberLauncherForActivityResult(QRScanner()) {
        it?.let {
            setAccountModel(accountModel.copy(password = it))
        }
    }

    KeyPassTheme {
        Scaffold(
            bottomBar = {
                BottomBar(
                    accountModel, backPressed = goBack,
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
            placeholder = R.string.account_name,
            value = accountModel.title,
            setValue = {
                updateAccountModel(accountModel.copy(title = it))
            }
        )

        KeyPassInputField(
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
                modifier = modifier,
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
                leadingIcon = if (accountModel.id != null)
                    null
                else (
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
            placeholder = R.string.tags_comma_separated_optional,
            value = accountModel.tags,
            setValue = {
                updateAccountModel(accountModel.copy(tags = it))
            }
        )

        KeyPassInputField(
            placeholder = R.string.website_url_optional,
            value = accountModel.site,
            setValue = {
                updateAccountModel(accountModel.copy(site = it))
            }
        )

        KeyPassInputField(
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
        modifier = modifier.fillMaxWidth(), value = value ?: "",
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

    val image = if (passwordVisible)
        Icons.Rounded.Visibility
    else
        Icons.Rounded.VisibilityOff

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
                IconButton(onClick = { openDialog.value = true }) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Rounded.Delete),
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onSaveClicked) {
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
