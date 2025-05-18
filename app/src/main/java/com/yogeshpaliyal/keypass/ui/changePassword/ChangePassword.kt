package com.yogeshpaliyal.keypass.ui.changePassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yogeshpaliyal.common.data.UserSettings
import com.yogeshpaliyal.common.utils.setKeyPassPassword
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.auth.components.PasswordInputField
import com.yogeshpaliyal.keypass.ui.commonComponents.DefaultBottomAppBar
import com.yogeshpaliyal.keypass.ui.commonComponents.DefaultTopAppBar
import com.yogeshpaliyal.keypass.ui.nav.LocalUserSettings
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.GoBackAction
import com.yogeshpaliyal.keypass.ui.redux.actions.StateUpdateAction
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import com.yogeshpaliyal.keypass.ui.redux.states.ChangeAppPasswordState
import kotlinx.coroutines.launch
import org.reduxkotlin.TypedDispatcher
import org.reduxkotlin.compose.rememberTypedDispatcher

fun TypedDispatcher<Action>.updateState(state: ChangeAppPasswordState) {
    this(StateUpdateAction(state = state))
}

@Composable
fun ChangePassword(state: ChangeAppPasswordState) {
    val dispatchAction = rememberTypedDispatcher<Action>()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val userSettings = LocalUserSettings.current
    
    // Track form validity
    val isFormValid by remember(state) {
        derivedStateOf {
            state.oldPassword.password.isNotBlank() &&
            state.newPassword.password.isNotBlank() &&
            state.confirmPassword.password.isNotBlank() &&
            state.newPassword.password == state.confirmPassword.password
        }
    }
    
    // Password strength indicators
    val passwordStrength by remember(state.newPassword.password) {
        derivedStateOf {
            when {
                state.newPassword.password.length < 8 -> 0.25f to "Weak"
                state.newPassword.password.length < 12 -> 0.5f to "Medium"
                state.newPassword.password.length < 16 -> 0.75f to "Strong"
                else -> 1f to "Very Strong"
            }
        }
    }
    
    var showInfoCard by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DefaultTopAppBar(
                showBackButton = true,
                title = R.string.change_app_password,
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        Surface(modifier = Modifier.padding(contentPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_undraw_unlock_24mb),
                    contentDescription = "Change Password Illustration",
                    modifier = Modifier
                        .size(160.dp)
                        .padding(vertical = 8.dp)
                )
                
                // Info button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { showInfoCard = !showInfoCard }) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = "Information",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Information card
                if (showInfoCard) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Password Tips",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "• Use at least 12 characters for better security\n" +
                                      "• Mix letters, numbers, and special characters\n" +
                                      "• Avoid using easily guessable information\n" +
                                      "• Don't reuse passwords from other sites",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Password input fields in a card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Old password
                        Text(
                            text = "Current Password",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        PasswordInputField(
                            R.string.old_password,
                            state.oldPassword.password,
                            { dispatchAction.updateState(
                                state.copy(
                                    oldPassword = state.oldPassword.copy(
                                        password = it,
                                        passwordError = null
                                    )
                                )
                            ) },
                            state.oldPassword.passwordVisible,
                            { dispatchAction.updateState(
                                state.copy(
                                    oldPassword = state.oldPassword.copy(
                                        passwordVisible = it
                                    )
                                )
                            ) },
                            state.oldPassword.passwordError
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // New password section
                        Text(
                            text = "New Password",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        PasswordInputField(
                            R.string.new_password,
                            state.newPassword.password,
                            { dispatchAction.updateState(
                                state.copy(
                                    newPassword = state.newPassword.copy(
                                        password = it,
                                        passwordError = null
                                    )
                                )
                            ) },
                            state.newPassword.passwordVisible,
                            { dispatchAction.updateState(
                                state.copy(
                                    newPassword = state.newPassword.copy(
                                        passwordVisible = it
                                    )
                                )
                            ) },
                            state.newPassword.passwordError
                        )
                        
                        // Password strength indicator
                        if (state.newPassword.password.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Password Strength:", 
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = passwordStrength.second,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = when(passwordStrength.second) {
                                            "Weak" -> MaterialTheme.colorScheme.error
                                            "Medium" -> MaterialTheme.colorScheme.tertiary
                                            "Strong" -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.secondary
                                        }
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                LinearProgressIndicator(
                                    progress = { passwordStrength.first },
                                    modifier = Modifier.fillMaxWidth().height(4.dp),
                                    color = when(passwordStrength.second) {
                                        "Weak" -> MaterialTheme.colorScheme.error
                                        "Medium" -> MaterialTheme.colorScheme.tertiary
                                        "Strong" -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.secondary
                                    }
                                )
                            }
                        }
                        
                        // Confirm password
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        PasswordInputField(
                            R.string.confirm_password,
                            state.confirmPassword.password,
                            { dispatchAction.updateState(
                                state.copy(
                                    confirmPassword = state.confirmPassword.copy(
                                        password = it,
                                        passwordError = null
                                    )
                                )
                            ) },
                            state.confirmPassword.passwordVisible,
                            { dispatchAction.updateState(
                                state.copy(
                                    confirmPassword = state.confirmPassword.copy(
                                        passwordVisible = it
                                    )
                                )
                            ) },
                            state.confirmPassword.passwordError
                        )

                        // Password match indicator
                        if (state.newPassword.password.isNotEmpty() && 
                            state.confirmPassword.password.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (state.newPassword.password == state.confirmPassword.password) 
                                                    Icons.Filled.CheckCircleOutline
                                                  else
                                                    Icons.Filled.ErrorOutline,
                                    contentDescription = "Password match status",
                                    tint = if (state.newPassword.password == state.confirmPassword.password) 
                                            MaterialTheme.colorScheme.primary 
                                          else
                                            MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (state.newPassword.password == state.confirmPassword.password) 
                                            "Passwords match" 
                                          else 
                                            "Passwords don't match",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (state.newPassword.password == state.confirmPassword.password) 
                                            MaterialTheme.colorScheme.primary 
                                          else
                                            MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
                
                // Action button for smaller screens
                Button(
                    onClick = {
                        validateAndChangePassword(
                            state = state,
                            dispatchAction = dispatchAction,
                            coroutineScope = coroutineScope,
                            userSettings = userSettings,
                            context = context
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid
                ) {
                    Text(
                        text = stringResource(id = R.string.change_app_password),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

private fun validateAndChangePassword(
    state: ChangeAppPasswordState,
    dispatchAction: TypedDispatcher<Action>,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    userSettings: UserSettings,
    context: android.content.Context
) {
    // Validate old password
    val oldPassword = state.oldPassword
    if (oldPassword.password.isBlank()) {
        dispatchAction.updateState(
            state.copy(
                oldPassword = oldPassword.copy(
                    passwordError = R.string.blank_old_password
                )
            )
        )
        return
    }

    // Validate new password
    val newPassword = state.newPassword
    if (newPassword.password.isBlank()) {
        dispatchAction.updateState(
            state.copy(
                newPassword = newPassword.copy(
                    passwordError = R.string.blank_new_password
                )
            )
        )
        return
    }

    // Validate confirm password
    val confirmPassword = state.confirmPassword
    if (confirmPassword.password.isBlank()) {
        dispatchAction.updateState(
            state.copy(
                confirmPassword = confirmPassword.copy(
                    passwordError = R.string.blank_confirm_password
                )
            )
        )
        return
    }

    coroutineScope.launch {
        // Check if old password is correct
        val oldPasswordFromStore = userSettings.keyPassPassword
        if (oldPassword.password != oldPasswordFromStore) {
            dispatchAction.updateState(
                state.copy(
                    oldPassword = oldPassword.copy(
                        passwordError = R.string.incorrect_old_password
                    )
                )
            )
            return@launch
        }

        // Check if new and confirm password match
        if (newPassword.password != confirmPassword.password) {
            dispatchAction.updateState(
                state.copy(
                    confirmPassword = confirmPassword.copy(
                        passwordError = R.string.password_no_match
                    )
                )
            )
            return@launch
        }

        // All validations passed, change the password
        context.setKeyPassPassword(newPassword.password)
        dispatchAction(ToastAction(R.string.password_change_success))
        dispatchAction(GoBackAction)
    }
}
