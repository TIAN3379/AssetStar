package com.example.assetstar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AssetStarColors = darkColorScheme(
    primary = AccentCyan,
    secondary = AccentBlue,
    tertiary = AccentLime,
    background = SpaceBlack,
    surface = PanelBlue,
    onPrimary = SpaceBlack,
    onSecondary = SoftWhite,
    onTertiary = SpaceBlack,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = TextSecondary,
)

@Composable
fun AssetStarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = AssetStarColors,
        typography = AssetStarTypography,
        content = content,
    )
}
