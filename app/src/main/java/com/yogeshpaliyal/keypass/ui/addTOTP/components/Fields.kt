package com.yogeshpaliyal.keypass.ui.addTOTP.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.commonComponents.KeyPassInputField

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
            KeyPassInputField(
                modifier = Modifier.testTag("secretKey"),
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
                }
            )
        }

        KeyPassInputField(
            modifier = Modifier.testTag("accountName"),
            placeholder = R.string.account_name,
            value = accountModel.title,
            setValue = {
                updateAccountModel(accountModel.copy(title = it))
            }
        )
    }
}
