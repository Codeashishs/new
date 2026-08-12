package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ChampagnePrimaryDark,
    onPrimary = Color(0xFF421E10),
    primaryContainer = Color(0xFF6A3B2A),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = RoseGoldSecondaryDark,
    tertiary = VelvetTertiaryDark,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = TextOnSurfaceDark,
    onSurface = TextOnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = ChampagnePrimary,
    onPrimary = Color.White,
    primaryContainer = SilkSurfaceVariantLight,
    onPrimaryContainer = ChampagnePrimary,
    secondary = RoseGoldSecondary,
    tertiary = VelvetTertiary,
    background = IvoryBackgroundLight,
    surface = IvorySurfaceLight,
    surfaceVariant = SilkSurfaceVariantLight,
    onBackground = TextOnSurfaceLight,
    onSurface = TextOnSurfaceLight
)

@Composable
fun GownScoutTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

