package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LuxuryNavyGoldColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = NavyDeep,
    primaryContainer = GoldSurface,
    onPrimaryContainer = GoldLight,
    secondary = BlueAccent,
    onSecondary = NavyDeep,
    secondaryContainer = BlueSurface,
    onSecondaryContainer = WhiteSoft,
    tertiary = GoldChampagne,
    onTertiary = NavyDeep,
    background = NavyDeep,
    onBackground = WhiteSoft,
    surface = NavyCard,
    onSurface = WhiteSoft,
    surfaceVariant = NavySurface,
    onSurfaceVariant = TextSecondary,
    outline = NavyBorder,
    error = ErrorRed,
    onError = WhitePure
)

val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LuxuryNavyGoldColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
