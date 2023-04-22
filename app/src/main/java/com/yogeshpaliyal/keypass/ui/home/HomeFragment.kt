package com.yogeshpaliyal.keypass.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yogeshpaliyal.common.constants.AccountType
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.addTOTP.AddTOTPActivity
import com.yogeshpaliyal.keypass.ui.detail.DetailActivity
import com.yogeshpaliyal.keypass.ui.style.KeyPassTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 09:25
*/
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val mViewModel by lazy {
        activityViewModels<DashboardViewModel>().value
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                Main(mViewModel)
            }
        }
    }
}

private fun getPassword(model: AccountModel): String {
    if (model.type == AccountType.TOTP) {
        return model.getOtp()
    }
    return model.password.orEmpty()
}

@Composable()
fun Main(mViewModel: DashboardViewModel = viewModel()) {
    KeyPassTheme {
        Surface(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.bottom_app_bar_height))) {
            val listOfAccountsLiveData by mViewModel.mediator.observeAsState()

            AccountsList(listOfAccountsLiveData)
        }
    }
}

@Composable
fun AccountsList(accounts: List<AccountModel>? = null) {
    val context = LocalContext.current

    if (accounts?.isNotEmpty() != null) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(accounts) { account ->
                Account(
                    account,
                    onClick = {
                        if (it.type == AccountType.TOTP) {
                            AddTOTPActivity.start(context, it.uniqueId)
                        } else {
                            DetailActivity.start(context, it.id)
                        }
                    },
                    onCopyClicked = {
                        val clipboard = ContextCompat.getSystemService(
                            context,
                            ClipboardManager::class.java
                        )
                        val clip = ClipData.newPlainText("KeyPass", getPassword(it))
                        clipboard?.setPrimaryClip(clip)
                        Toast.makeText(
                            context,
                            context.getString(R.string.copied_to_clipboard),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
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
            },
            onCopyClicked = {
            }
        )
    }
}

@Composable
fun Account(
    accountModel: AccountModel,
    onClick: (AccountModel) -> Unit,
    onCopyClicked: (AccountModel) -> Unit
) {
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
                onClick = { onCopyClicked(accountModel) }
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
                delay(1.seconds)
                val newProgress = accountModel.getTOtpProgress().toFloat() / 30
                setProgress(newProgress)
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
    val (username, setUsername) = remember { mutableStateOf(getUsernameOrOtp(accountModel)) }

    LaunchedEffect(Unit) {
        if (accountModel.type == AccountType.TOTP) {
            while (true) {
                delay(1.seconds)
                setUsername(accountModel.getOtp())
            }
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
