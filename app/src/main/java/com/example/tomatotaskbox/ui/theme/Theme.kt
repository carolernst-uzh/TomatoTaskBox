package com.example.tomatotaskbox.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


// Primary Color Palette
val TomatoRed = Color(0xFFFF6347)
val DarkTomatoRed = Color(0xFFCC5038)
val LightTomatoRed = Color(0xFFFF7A6B)

// Light Theme Colors
val md_theme_light_primary = TomatoRed
val md_theme_light_onPrimary = Color.White
val md_theme_light_secondary = DarkTomatoRed
val md_theme_light_onSecondary = Color.White
val md_theme_light_background = Color(0xFFFAFAFA)
val md_theme_light_onBackground = Color.Black
val md_theme_light_surface = Color.White
val md_theme_light_onSurface = Color.Black

// Dark Theme Colors
val md_theme_dark_primary = LightTomatoRed
val md_theme_dark_onPrimary = Color.White
val md_theme_dark_secondary = TomatoRed
val md_theme_dark_onSecondary = Color.White
val md_theme_dark_background = Color(0xFF121212)
val md_theme_dark_onBackground = Color.White
val md_theme_dark_surface = Color(0xFF1E1E1E)
val md_theme_dark_onSurface = Color.White


private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface
)

@Composable
fun TomatoTaskBoxTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (!useDarkTheme) {
        LightColors
    } else {
        DarkColors
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}