package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CardboardOSColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CyberDarkBg,
    primaryContainer = NeonViolet,
    onPrimaryContainer = TextPrimary,
    secondary = NeonPurple,
    onSecondary = TextPrimary,
    secondaryContainer = CyberSurfaceGlass,
    onSecondaryContainer = NeonCyan,
    tertiary = NeonPink,
    background = CyberDarkBg,
    onBackground = TextPrimary,
    surface = CyberSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceGlass,
    onSurfaceVariant = TextSecondary,
    outline = NeonCyanGlow
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CardboardOSColorScheme,
        typography = Typography,
        content = content
    )
}
