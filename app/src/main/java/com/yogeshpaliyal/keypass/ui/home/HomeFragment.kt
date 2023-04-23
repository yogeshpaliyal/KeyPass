package com.yogeshpaliyal.keypass.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.twotone.ContentCopy
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yogeshpaliyal.common.constants.AccountType
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.redux.AccountDetailState
import com.yogeshpaliyal.keypass.ui.redux.CopyToClipboard
import com.yogeshpaliyal.keypass.ui.redux.HomeState
import com.yogeshpaliyal.keypass.ui.redux.IntentNavigation
import com.yogeshpaliyal.keypass.ui.redux.NavigationAction
import com.yogeshpaliyal.keypass.ui.style.KeyPassTheme
import kotlinx.coroutines.delay
import org.reduxkotlin.compose.rememberDispatcher
import kotlin.time.Duration.Companion.seconds

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 09:25
*/
private fun getPassword(model: AccountModel): String {
    if (model.type == AccountType.TOTP) {
        return model.getOtp()
    }
    return model.password.orEmpty()
}

@Composable()
fun Homepage(mViewModel: DashboardViewModel = viewModel(), selectedTag: String?) {
    val listOfAccountsLiveData by mViewModel.mediator.observeAsState()

    val keyword by mViewModel.keyword.observeAsState()
    val dispatchAction = rememberDispatcher()

    LaunchedEffect(key1 = selectedTag, block = {
        if (selectedTag.isNullOrBlank()) {
            mViewModel.tag.postValue(null)
        } else {
            mViewModel.tag.postValue(selectedTag)
        }
    })

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(16.dp),
            value = keyword ?: "",
            placeholder = {
                Text(text = "Search Account")
            },
            onValueChange = { newValue -> mViewModel.keyword.value = newValue },
            trailingIcon = {
                if (keyword.isNullOrBlank().not()) {
                    IconButton(onClick = { mViewModel.keyword.value = "" }) {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Rounded.Close),
                            contentDescription = ""
                        )
                    }
                }
            }
        )

        if (selectedTag != null) {
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                content = {
                    item {
                        AssistChip(onClick = { }, label = {
                            Text(text = selectedTag)
                        }, trailingIcon = {
                                IconButton(onClick = {
                                    dispatchAction(NavigationAction(HomeState(), true))
                                }) {
                                    Icon(
                                        painter = rememberVectorPainter(image = Icons.Rounded.Close),
                                        contentDescription = ""
                                    )
                                }
                            })
                    }
                }
            )
        }

        AccountsList(listOfAccountsLiveData)
    }
}

@Composable
fun AccountsList(accounts: List<AccountModel>? = null) {
    val dispatch = rememberDispatcher()

    if (accounts?.isNotEmpty() == true) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(accounts) { account ->
                Account(
                    account,
                    onClick = {
                        if (it.type == AccountType.TOTP) {
                            dispatch(IntentNavigation.AddTOTP(it.uniqueId))
                        } else {
                            dispatch(NavigationAction(AccountDetailState(it.id)))
                        }
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    } else {
        NoDataFound()
    }
}

@Preview
@Composable
fun PreviewAccount() {
    KeyPassTheme {
        Account(
            accountModel = AccountModel(),
            onClick = {
            }
        )
    }
}

@Composable
fun Account(
    accountModel: AccountModel,
    onClick: (AccountModel) -> Unit
) {
    val dispatch = rememberDispatcher()

    Card(
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = { onClick(accountModel) }
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(60.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50)
                    )
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = accountModel.getInitials(),
                    textAlign = TextAlign.Center
                )

                if (accountModel.type == AccountType.TOTP) {
                    WrapWithProgress(accountModel)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = accountModel.title ?: "",
                    style = MaterialTheme.typography.headlineSmall.merge(
                        TextStyle(
                            fontSize = 16.sp
                        )
                    )
                )

                RenderUserName(accountModel)
            }

            IconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = { dispatch(CopyToClipboard(getPassword(accountModel))) }
            ) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.TwoTone.ContentCopy),
                    contentDescription = "Copy To Clipboard"
                )
            }
        }
    }
}

@Composable
fun WrapWithProgress(accountModel: AccountModel) {
    val (progress, setProgress) = remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        if (accountModel.type == AccountType.TOTP) {
            while (true) {
                val newProgress = accountModel.getTOtpProgress().toFloat() / 30
                setProgress(newProgress)
                delay(1.seconds)
            }
        }
    }

    CircularProgressIndicator(
        modifier = Modifier.fillMaxSize(),
        progress = progress
    )
}

private fun getUsernameOrOtp(accountModel: AccountModel): String? {
    return if (accountModel.type == AccountType.TOTP) accountModel.getOtp() else accountModel.username
}

@Composable
fun RenderUserName(accountModel: AccountModel) {
    val (username, setUsername) = remember { mutableStateOf("") }

    LaunchedEffect(accountModel) {
        if (accountModel.type == AccountType.TOTP) {
            while (true) {
                setUsername(accountModel.getOtp())
                delay(1.seconds)
            }
        } else {
            setUsername(accountModel.username ?: "")
        }
    }

    Text(
        text = username ?: "",
        style = MaterialTheme.typography.bodyMedium.merge(
            TextStyle(
                fontSize = 14.sp
            )
        )
    )
}

@Composable
fun NoDataFound() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.message_no_accounts),
                modifier = Modifier
                    .padding(32.dp)
                    .align(alignment = Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
        Image(
            painter = painterResource(R.drawable.ic_undraw_empty_street_sfxm),
            contentDescription = ""
        )
    }
}
