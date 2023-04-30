package com.yogeshpaliyal.keypass.ui.generate.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.generate.GeneratePasswordViewModel
import com.yogeshpaliyal.keypass.ui.generate.ui.utils.copyTextToClipboard

@Composable
fun GeneratePasswordScreen(viewModel: GeneratePasswordViewModel) {
    val context = LocalContext.current

    // replace collectAsState() with collectAsStateWithLifecycle() when compose version and kotlin version are bumped up.
    val viewState by viewModel.viewState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.generatePassword()
    }

    GeneratePasswordContent(
        viewState = viewState,
        onGeneratePasswordClick = viewModel::generatePassword,
        onCopyPasswordClick = { onCopyPasswordClick(context, viewState.password) },
        onPasswordLengthChange = viewModel::onPasswordLengthSliderChange,
        onUppercaseCheckedChange = viewModel::onUppercaseCheckedChange,
        onLowercaseCheckedChange = viewModel::onLowercaseCheckedChange,
        onNumbersCheckedChange = viewModel::onNumbersCheckedChange,
        onSymbolsCheckedChange = viewModel::onSymbolsCheckedChange
    )
}

private fun onCopyPasswordClick(context: Context, text: String) {
    copyTextToClipboard(context = context, text = text, label = "random_password")
    Toast
        .makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT)
        .show()
}
