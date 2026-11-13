package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MacumuDarkColorScheme = darkColorScheme(
    primary = MacumuOrange,
    onPrimary = Color.White,
    primaryContainer = MacumuOrangeContainer,
    onPrimaryContainer = MacumuOnOrangeContainer,
    secondary = EmolaBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF0F3254),
    onSecondaryContainer = Color(0xFFC7E6FF),
    tertiary = SuccessGreen,
    onTertiary = Color.White,
    background = DarkNavyBackground,
    onBackground = TextWhite,
    surface = DarkNavySurface,
    onSurface = TextWhite,
    surfaceVariant = DarkNavyCard,
    onSurfaceVariant = TextMuted,
    outline = DarkNavyCardBorder,
    outlineVariant = Color(0xFF1E283C),
    error = DangerRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to dark mode as requested
    dynamicColor: Boolean = false, // Keep Macumu Global brand identity consistent
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MacumuDarkColorScheme,
        typography = Typography,
        content = content
    )
}
