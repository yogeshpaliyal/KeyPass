package com.yogeshpaliyal.keypass.ui.generate.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.generate.GeneratePasswordViewModel
import com.yogeshpaliyal.keypass.ui.generate.ui.utils.copyTextToClipboard

@Composable
fun GeneratePasswordScreen(viewModel: GeneratePasswordViewModel = hiltViewModel()) {
    val context = LocalContext.current

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.retrieveSavedPasswordConfig(context)
    }

    GeneratePasswordContent(
        viewState = viewState,
        onGeneratePasswordClick = viewModel::generatePassword,
        onCopyPasswordClick = { onCopyPasswordClick(context, viewState.password) },
        onPasswordLengthChange = viewModel::onPasswordLengthSliderChange,
        onUppercaseCheckedChange = viewModel::onUppercaseCheckedChange,
        onLowercaseCheckedChange = viewModel::onLowercaseCheckedChange,
        onNumbersCheckedChange = viewModel::onNumbersCheckedChange,
        onSymbolsCheckedChange = viewModel::onSymbolsCheckedChange,
        selectSymbolForPassword = viewModel::selectSymbolForPassword,
        onBlankSpacesCheckedChange = viewModel::onBlankSpacesCheckedChange
    )
}

private fun onCopyPasswordClick(context: Context, text: String) {
    copyTextToClipboard(context = context, text = text, label = "random_password")
    Toast
        .makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT)
        .show()
}
