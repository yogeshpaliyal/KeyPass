package com.yogeshpaliyal.keypass.ui.generate.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.yogeshpaliyal.common.data.PasswordConfig
import com.yogeshpaliyal.common.utils.PasswordGenerator
import com.yogeshpaliyal.keypass.ui.generate.ui.components.CheckboxWithLabel
import com.yogeshpaliyal.keypass.ui.generate.ui.components.PasswordLengthInput

@Composable
fun GeneratePasswordContent(
    viewState: PasswordConfig,
    onCopyPasswordClick: () -> Unit,
    onGeneratePasswordClick: () -> Unit,
    onPasswordLengthChange: (Float) -> Unit,
    onUppercaseCheckedChange: (Boolean) -> Unit,
    onLowercaseCheckedChange: (Boolean) -> Unit,
    onNumbersCheckedChange: (Boolean) -> Unit,
    onSymbolsCheckedChange: (Boolean) -> Unit,
    selectSymbolForPassword: (Char) -> Unit = {},
    onBlankSpacesCheckedChange: (Boolean) -> Unit
) {
    Scaffold(
        floatingActionButton = { GeneratePasswordFab(onGeneratePasswordClick) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            FormInputCard(
                viewState = viewState,
                onCopyPasswordClick = onCopyPasswordClick,
                onPasswordLengthChange = onPasswordLengthChange,
                onUppercaseCheckedChange = onUppercaseCheckedChange,
                onLowercaseCheckedChange = onLowercaseCheckedChange,
                onNumbersCheckedChange = onNumbersCheckedChange,
                onSymbolsCheckedChange = onSymbolsCheckedChange,
                selectSymbolForPassword = selectSymbolForPassword,
                onBlankSpacesCheckedChange = onBlankSpacesCheckedChange
            )
        }
    }
}

@Composable
private fun GeneratePasswordFab(onGeneratePasswordClick: () -> Unit) {
    FloatingActionButton(
        onClick = onGeneratePasswordClick,
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = ""
        )
    }
}

@Composable
private fun FormInputCard(
    viewState: PasswordConfig,
    onCopyPasswordClick: () -> Unit,
    onPasswordLengthChange: (Float) -> Unit,
    onUppercaseCheckedChange: (Boolean) -> Unit,
    onLowercaseCheckedChange: (Boolean) -> Unit,
    onNumbersCheckedChange: (Boolean) -> Unit,
    onSymbolsCheckedChange: (Boolean) -> Unit,
    selectSymbolForPassword: (Char) -> Unit = {},
    onBlankSpacesCheckedChange: (Boolean) -> Unit
) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PasswordTextField(viewState.password, onCopyPasswordClick)

            // temporary label until we put slider label on the thumb to display current value.
            PasswordLengthInput(viewState.length, onPasswordLengthChange)

            UppercaseAlphabetInput(viewState.includeUppercaseLetters, onUppercaseCheckedChange)

            LowercaseAlphabetInput(viewState.includeLowercaseLetters, onLowercaseCheckedChange)

            NumberInput(viewState.includeNumbers, onNumbersCheckedChange)

            SymbolInput(viewState.includeSymbols, onSymbolsCheckedChange, selectSymbolForPassword, viewState.listOfSymbols)

            BlankSpaceInput(viewState.includeBlankSpaces, onBlankSpacesCheckedChange)
        }
    }
}

@Composable
private fun PasswordTextField(
    password: String,
    onCopyPasswordClick: () -> Unit
) {
    OutlinedTextField(
        value = password,
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            IconButton(
                onClick = onCopyPasswordClick
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy password"
                )
            }
        },
        label = {
            Text(text = "Password")
        }
    )
}

@Composable
private fun UppercaseAlphabetInput(
    includeUppercaseLetters: Boolean,
    onUppercaseCheckedChange: (Boolean) -> Unit
) {
    CheckboxWithLabel(
        label = "Uppercase Alphabets",
        checked = includeUppercaseLetters,
        onCheckedChange = onUppercaseCheckedChange
    )
}

@Composable
private fun LowercaseAlphabetInput(
    includeLowercaseLetters: Boolean,
    onLowercaseCheckedChange: (Boolean) -> Unit
) {
    CheckboxWithLabel(
        label = "Lowercase Alphabets",
        checked = includeLowercaseLetters,
        onCheckedChange = onLowercaseCheckedChange
    )
}

@Composable
private fun NumberInput(
    includeNumbers: Boolean,
    onNumbersCheckedChange: (Boolean) -> Unit
) {
    CheckboxWithLabel(
        label = "Numbers",
        checked = includeNumbers,
        onCheckedChange = onNumbersCheckedChange
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SymbolInput(
    includeSymbols: Boolean,
    onSymbolsCheckedChange: (Boolean) -> Unit,
    selectSymbolForPassword: (Char) -> Unit = {},
    selectedSymbols: List<Char>
) {
    Column {
        CheckboxWithLabel(
            label = "Symbols",
            checked = includeSymbols,
            onCheckedChange = onSymbolsCheckedChange
        )
        if (includeSymbols) {
            FlowRow(modifier = Modifier.fillMaxWidth()) {
                OutlinedCard(
                    onClick = {
                        selectSymbolForPassword('s')
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (selectedSymbols.size == PasswordGenerator.totalSymbol.size) MaterialTheme.colorScheme.primary else Color.Unspecified
                    )
                ) {
                    Text(
                        text = "All",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                PasswordGenerator.totalSymbol.forEach { symbol ->
                    OutlinedCard(
                        onClick = {
                            selectSymbolForPassword(symbol)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.outlinedCardColors(
                            if (symbol in selectedSymbols && selectedSymbols.size != PasswordGenerator.totalSymbol.size) MaterialTheme.colorScheme.primary else Color.Unspecified
                        )
                    ) {
                        Text(
                            text = "$symbol",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BlankSpaceInput(
    includeBlankSpaces: Boolean,
    onBlankSpacesCheckedChange: (Boolean) -> Unit
) {
    CheckboxWithLabel(
        label = "Blank Spaces",
        checked = includeBlankSpaces,
        onCheckedChange = onBlankSpacesCheckedChange
    )
}

@Preview(
    name = "Night Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Day Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
@Suppress("UnusedPrivateMember", "MagicNumber")
private fun GeneratePasswordContentPreview() {
    val viewState = PasswordConfig.Initial

    Mdc3Theme {
        GeneratePasswordContent(
            viewState = viewState,
            onGeneratePasswordClick = {},
            onCopyPasswordClick = {},
            onPasswordLengthChange = {},
            onUppercaseCheckedChange = {},
            onLowercaseCheckedChange = {},
            onNumbersCheckedChange = {},
            onSymbolsCheckedChange = {},
            onBlankSpacesCheckedChange = {}
        )
    }
}
