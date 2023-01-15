package com.yogeshpaliyal.keypasscompose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColorScheme(
    primary = KeyPassBlue700,
    primaryContainer = KeyPassOrange500,
    secondary = KeyPassOrange500
)

private val LightColorPalette = lightColorScheme(
    primary = KeyPassBlue700,
    primaryContainer = KeyPassOrange500,
    secondary = KeyPassOrange500,
    tertiaryContainer = Blue800,
    background = KeyPassBlue50,
    surface = KeyPassWhite50,
    error = KeyPassRed400,
    onPrimary = KeyPassWhite50,
    onSecondary = KeyPassBlack900,
    onSurface = KeyPassBlack900,
    onError = KeyPassBlack900

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun KeyPassTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
