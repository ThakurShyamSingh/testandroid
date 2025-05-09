// File: ui/theme/Theme.kt
package com.example.demotest2.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColorScheme = lightColorScheme(
    primary = BlueTrue,
    secondary = RedAccent,
    background = BackgroundLight,
    surface = BackgroundCardLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextLight,
    onSurface = TextLight
)

private val DarkColorScheme = darkColorScheme(
    primary = BlueAccentDark,
    secondary = RedAccentDark,
    background = BackgroundDark,
    surface = BackgroundCardDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = TextDark,
    onSurface = TextDark
)

// Global state for theme toggle
var isDarkTheme by mutableStateOf(false)

/**
 * Applies system-wide theming with status/nav bar sync.
 */
@Composable
fun DemoTest2Theme(
    content: @Composable () -> Unit
) {
    val darkTheme = isDarkTheme
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = colorScheme.background,
            darkIcons = !darkTheme
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
