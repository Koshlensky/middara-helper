package com.middara.helper.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MiddaraDarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = Color(0xFF1A1A00),
    primaryContainer = Color(0xFF3A2A00),
    onPrimaryContainer = GoldLight,
    secondary = CrimsonAccent,
    onSecondary = Color.White,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextSecondary,
    outline = DarkCardBorder,
    error = Color(0xFFCF6679),
    onError = Color.White
)

@Composable
fun MiddaraHelperTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MiddaraDarkColorScheme,
        typography = Typography,
        content = content
    )
}
