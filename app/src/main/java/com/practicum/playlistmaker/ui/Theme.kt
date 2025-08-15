package com.practicum.playlistmaker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.practicum.playlistmaker.R

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3772E7),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFF5F5F5),
    onSecondary = Color(0xFF1A1B22),
    surface = Color(0xFFAEAFB4),
    onSurface = Color(0xFFD9DAE2),
    tertiary = Color(0xFFE6E8EB),
    onTertiary = Color(0xFF9FBBF3)
)

val YsFontFamily = FontFamily(
    Font(R.font.ys_display_medium, weight = FontWeight.Medium)
)


@Composable
fun PlaylistMakerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}