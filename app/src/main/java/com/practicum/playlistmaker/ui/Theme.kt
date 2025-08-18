package com.practicum.playlistmaker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R

private val BluePrimary = Color(0xFF3772E7)

private val TextLight = Color(0xFF1A1B22)
private val BgLight = Color(0xFFFFFFFF)
private val SurfaceVarLight = Color(0xFFE6E8EB)
private val OnSurfaceVarLight = Color(0xFFAEAFB4)
private val SecondaryLight = Color(0xFFF5F5F5)

private val BgDark = Color(0xFF1A1B22)
private val TextDark = Color(0xFFFFFFFF)


private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,

    background = BgLight,
    onBackground = TextLight,

    surface = BgLight,
    onSurface = TextLight,

    surfaceVariant = SurfaceVarLight,
    onSurfaceVariant = OnSurfaceVarLight,
    outlineVariant = OnSurfaceVarLight,

    secondary = SecondaryLight,
    onSecondary = TextLight
)

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,

    background = BgDark,
    onBackground = TextDark,

    surface = BgDark,
    onSurface = TextDark,

    surfaceVariant = BgDark.copy(alpha = 0.92f),
    onSurfaceVariant = OnSurfaceVarLight,
    outlineVariant = OnSurfaceVarLight,

    secondary = BgDark,
    onSecondary = TextDark
)

val YsFontFamily = FontFamily(
    Font(R.font.ys_display_regular, FontWeight.W400),
    Font(R.font.ys_display_medium, FontWeight.W500)
)

private val AppTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = YsFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = YsFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 19.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = YsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = YsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    bodySmall = TextStyle(
        fontFamily = YsFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 13.sp,
        lineHeight = 16.sp
    )
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp)
)

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
