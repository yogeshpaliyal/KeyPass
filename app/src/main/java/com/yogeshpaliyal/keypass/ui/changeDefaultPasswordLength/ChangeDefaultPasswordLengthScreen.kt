package com.yogeshpaliyal.keypass.ui.changeDefaultPasswordLength

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    Scaffold(bottomBar = { BottomBar(dispatchAction, viewModel) }) { contentPadding ->
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PasswordLengthInput(length = state.length, viewModel::onPasswordLengthChanged)
    }
}

@Composable
private fun BottomBar(
    dispatchAction: TypedDispatcher<Action>,
    viewModel: ChangeDefaultPasswordLengthViewModel
) {
    val context = LocalContext.current
    BottomAppBar(
        actions = {
            IconButton(onClick = { dispatchAction(GoBackAction) }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.ArrowBackIosNew),
                    contentDescription = "Go Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(modifier = Modifier.testTag("save"), onClick = {
                // Save new password length
                viewModel.updatePasswordLength(context) {
                    // Close screen
                    dispatchAction(GoBackAction)
                }
            }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.Done),
                    contentDescription = "Save Changes"
                )
            }
        }
    )
}

@Preview(name = "ChangeDefaultPasswordLength", showBackground = true, showSystemUi = true)
@Composable
fun ChangeDefaultPasswordLengthPreview() {
    ChangeDefaultPasswordLengthScreen()
}
