package com.yogeshpaliyal.keypass.ui.style

import androidx.compose.runtime.Composable
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun KeyPassTheme(content: @Composable () -> Unit) {
    Mdc3Theme {
        content()
    }
}
