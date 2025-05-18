package com.yogeshpaliyal.keypass.ui.changeDefaultPasswordLength

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.commonComponents.DefaultTopAppBar
import com.yogeshpaliyal.keypass.ui.generate.ui.components.PasswordLengthInput
import com.yogeshpaliyal.keypass.ui.redux.actions.Action
import com.yogeshpaliyal.keypass.ui.redux.actions.GoBackAction
import com.yogeshpaliyal.keypass.ui.redux.states.ChangeDefaultPasswordLengthState
import org.reduxkotlin.TypedDispatcher
import org.reduxkotlin.compose.rememberTypedDispatcher

@Composable
fun ChangeDefaultPasswordLengthScreen(
    viewModel: ChangeDefaultPasswordLengthViewModel = viewModel()
) {
    viewModel.retrieveSavedPasswordLength(LocalContext.current)
    val dispatchAction = rememberTypedDispatcher<Action>()
    val state by viewModel.viewState.collectAsState()

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = R.string.choose_default_password_length,
                subtitle = R.string.choose_default_password_length_desc
            )
        },
        floatingActionButton = { FloatingActionButton(dispatchAction, viewModel) }
    ) { contentPadding ->
        Surface(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth()
        ) {
            ChangeDefaultPasswordLengthContent(state = state, viewModel = viewModel)
        }
    }
}

@Composable
private fun ChangeDefaultPasswordLengthContent(
    state: ChangeDefaultPasswordLengthState,
    viewModel: ChangeDefaultPasswordLengthViewModel
) {
    val passwordStrength by remember(state.length) {
        derivedStateOf {
            getPasswordStrength(state.length)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Password length input with improved UI

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Current Length: ${state.length.toInt()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                PasswordLengthInput(length = state.length, viewModel::onPasswordLengthChanged)

                Spacer(modifier = Modifier.height(16.dp))

                // Password strength indicator
                PasswordStrengthIndicator(passwordStrength)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

enum class PasswordStrength(val label: String, val progress: Float) {
    WEAK("Weak", 0.25f),
    MEDIUM("Medium", 0.5f),
    STRONG("Strong", 0.75f),
    VERY_STRONG("Very Strong", 1f)
}


private fun getPasswordStrength(length: Float): PasswordStrength {
    return when {
        length < 8 -> PasswordStrength.WEAK
        length < 12 -> PasswordStrength.MEDIUM
        length < 32 -> PasswordStrength.STRONG
        else -> PasswordStrength.VERY_STRONG
    }
}

@Composable
private fun getStrengthColor(strength: PasswordStrength): Color {
    return when (strength) {
        PasswordStrength.WEAK -> MaterialTheme.colorScheme.error
        PasswordStrength.MEDIUM -> MaterialTheme.colorScheme.tertiary
        PasswordStrength.STRONG -> MaterialTheme.colorScheme.primary
        PasswordStrength.VERY_STRONG -> MaterialTheme.colorScheme.secondary
    }
}

@Composable
private fun PasswordStrengthIndicator(strength: PasswordStrength) {
    val strengthColor = getStrengthColor(strength)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Password Strength",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = strength.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = strengthColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { strength.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = strengthColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun FloatingActionButton(
    dispatchAction: TypedDispatcher<Action>,
    viewModel: ChangeDefaultPasswordLengthViewModel
) {
    val context = LocalContext.current

    FloatingActionButton(
        modifier = Modifier.testTag("save"),
        onClick = {
            // Save new password length
            viewModel.updatePasswordLength(context) {
                // Close screen
                dispatchAction(GoBackAction)
            }
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Icon(
            painter = rememberVectorPainter(image = Icons.Rounded.Done),
            contentDescription = "Save Changes",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Preview(name = "ChangeDefaultPasswordLength", showBackground = true, showSystemUi = true)
@Composable
fun ChangeDefaultPasswordLengthPreview() {
    ChangeDefaultPasswordLengthScreen()
}
