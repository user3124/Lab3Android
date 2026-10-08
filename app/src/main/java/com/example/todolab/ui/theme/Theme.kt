package com.example.todolab.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    secondary = CoralButton,
    onSecondary = Color.White,
    tertiary = SkyButton,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextDark,
    surface = CardWhite,
    onSurface = TextDark,
    onSurfaceVariant = TextMuted,
    outline = DividerLight
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun TodoLabTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}