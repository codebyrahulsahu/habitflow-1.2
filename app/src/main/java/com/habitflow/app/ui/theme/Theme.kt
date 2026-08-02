package com.habitflow.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = SageGreen,
    secondary = SoftSkyBlue,
    background = WarmCream,
    surface = SoftWhite,
    onBackground = CharcoalGrey,
    onSurface = CharcoalGrey,
    tertiary = WarmSand
)

private val DarkColors = darkColorScheme(
    primary = SageGreenDark,
    secondary = DustyBlue,
    background = DeepCharcoalNavy,
    surface = SlateGrey,
    onBackground = SoftOffWhite,
    onSurface = SoftOffWhite,
    tertiary = WarmSand
)

@Composable
fun HabitFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
