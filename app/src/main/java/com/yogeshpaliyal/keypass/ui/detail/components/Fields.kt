package com.yogeshpaliyal.keypass.ui.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.common.constants.ScannerType
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.utils.PasswordGenerator
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.commonComponents.KeyPassInputField
import com.yogeshpaliyal.keypass.ui.commonComponents.PasswordTrailingIcon

@Composable
fun Fields(
    modifier: Modifier = Modifier,
    accountModel: AccountModel,
    updateAccountModel: (newAccountModel: AccountModel) -> Unit,
    copyToClipboardClicked: (String) -> Unit,
    scanClicked: (scannerType: Int) -> Unit
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
            },
            copyToClipboardClicked = copyToClipboardClicked
        )

        KeyPassInputField(
            modifier = Modifier.testTag("username"),
            placeholder = R.string.username_email_phone,
            value = accountModel.username,
            setValue = {
                updateAccountModel(accountModel.copy(username = it))
            },
            copyToClipboardClicked = copyToClipboardClicked
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
                visualTransformation = visualTransformation,
                copyToClipboardClicked = copyToClipboardClicked
            )
            Button(onClick = { scanClicked(ScannerType.Password) }) {
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
            modifier = Modifier.testTag("secretKey"),
            placeholder = R.string.secret_key,
            value = accountModel.secret,
            setValue = {
                updateAccountModel(accountModel.copy(secret = it))
            },
            trailingIcon = {
                IconButton(onClick = {
                    scanClicked(ScannerType.Secret)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_twotone_qr_code_scanner_24),
                        contentDescription = ""
                    )
                }
            }
        )

        KeyPassInputField(
            modifier = Modifier.testTag("tags"),
            placeholder = R.string.tags_comma_separated_optional,
            value = accountModel.tags,
            setValue = {
                updateAccountModel(accountModel.copy(tags = it))
            },
            copyToClipboardClicked = copyToClipboardClicked
        )

        KeyPassInputField(
            modifier = Modifier.testTag("website"),
            placeholder = R.string.website_url_optional,
            value = accountModel.site,
            setValue = {
                updateAccountModel(accountModel.copy(site = it))
            },
            copyToClipboardClicked = copyToClipboardClicked
        )

        KeyPassInputField(
            modifier = Modifier.testTag("notes"),
            placeholder = R.string.notes_optional,
            value = accountModel.notes,
            setValue = {
                updateAccountModel(accountModel.copy(notes = it))
            },
            copyToClipboardClicked = copyToClipboardClicked
        )
    }
}
