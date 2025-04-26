package com.yogeshpaliyal.keypass.ui.passwordHint

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.common.utils.setPasswordHint
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.commonComponents.DefaultBottomAppBar
import com.yogeshpaliyal.keypass.ui.commonComponents.KeyPassInputField
import com.yogeshpaliyal.keypass.ui.nav.LocalUserSettings
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.BatchActions
import com.yogeshpaliyal.keypass.ui.redux.actions.GoBackAction
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import kotlinx.coroutines.launch
import org.reduxkotlin.compose.rememberTypedDispatcher

@Composable
fun PasswordHintScreen() {
    val dispatchAction = rememberTypedDispatcher<Action>()

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val userSettings = LocalUserSettings.current
    val (passwordHint, setPasswordHint) = rememberSaveable { mutableStateOf(userSettings.passwordHint ?: "") }
    var showRemoveDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { 
            DefaultBottomAppBar(
                showBackButton = true
            ) 
        }
    ) { contentPadding ->
        Surface(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title and Description
                Text(
                    text = stringResource(id = R.string.app_password_hint),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "A password hint helps you remember your password if you forget it. " +
                           "Make sure it's a hint only you would understand.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                // Input Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Help icon and text
                        TextButton(
                            onClick = { showInfoDialog = true },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.HelpOutline,
                                contentDescription = "Help",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "What makes a good hint?",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        
                        // Input field
                        KeyPassInputField(
                            placeholder = R.string.enter_password_hint,
                            value = passwordHint,
                            setValue = setPasswordHint,
                        )
                        
                        // Current status
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (userSettings.passwordHint != null) 
                                    MaterialTheme.colorScheme.tertiaryContainer 
                                else 
                                    MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = if (userSettings.passwordHint != null) 
                                    "You currently have a password hint set" 
                                else 
                                    "You don't have a password hint yet",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp),
                                color = if (userSettings.passwordHint != null) 
                                    MaterialTheme.colorScheme.onTertiaryContainer 
                                else 
                                    MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                // Buttons
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        coroutineScope.launch {
                            context.setPasswordHint(passwordHint)
                            dispatchAction(BatchActions(GoBackAction, ToastAction(R.string.hint_change_success)))
                        }
                    },
                    enabled = passwordHint.isNotBlank()
                ) {
                    Text(
                        text = if (userSettings.passwordHint != null) 
                            stringResource(id = R.string.change_app_hint) 
                        else 
                            stringResource(id = R.string.set_app_password_hint),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                if (userSettings.passwordHint != null) {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showRemoveDialog = true }
                    ) {
                        Text(
                            text = stringResource(id = R.string.remove_app_hint),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
        
        // Remove hint confirmation dialog
        if (showRemoveDialog) {
            AlertDialog(
                onDismissRequest = { showRemoveDialog = false },
                title = { Text("Remove Password Hint") },
                text = { 
                    Text(
                        "Are you sure you want to remove your password hint? " +
                        "If you forget your password, you won't have a hint to help you remember it."
                    ) 
                },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                context.setPasswordHint(null)
                                dispatchAction(BatchActions(GoBackAction, ToastAction(R.string.hint_removed_success)))
                            }
                        }
                    ) {
                        Text("Remove")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRemoveDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // Info dialog about hints
        if (showInfoDialog) {
            AlertDialog(
                onDismissRequest = { showInfoDialog = false },
                title = { Text("Password Hint Tips") },
                text = { 
                    Column {
                        Text(
                            "A good password hint should:",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Text("• Be meaningful only to you")
                        Text("• Not reveal your actual password")
                        Text("• Remind you of your password pattern")
                        Text("• Avoid personal information others might know")
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            "Examples:",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Text("• \"First pet + year\"")
                        Text("• \"Favorite movie character\"")
                        Text("• \"Library book title\"")
                    }
                },
                confirmButton = {
                    Button(onClick = { showInfoDialog = false }) {
                        Text("Got it")
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun PPasswordHintScreen() {
    PasswordHintScreen()
}
