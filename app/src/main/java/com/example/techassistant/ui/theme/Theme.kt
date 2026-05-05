package com.example.techassistant.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = TextPrimary,
    secondary = UserBubbleDark,
    surface = BackgroundDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = TextSecondary,
    background = BackgroundDark,
    onBackground = TextPrimary,
    error = androidx.compose.ui.graphics.Color(0xFFF2B8B5),
    onError = androidx.compose.ui.graphics.Color(0xFF601410),
    errorContainer = androidx.compose.ui.graphics.Color(0xFF8C1D18),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFFF9DEDC)
)

private val LightColorScheme = lightColorScheme(
    primary = AIBlue,
    onPrimary = LightGray,
    secondary = AISilver,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = SoftBlack,
    surfaceVariant = LightGray,
    onSurfaceVariant = DarkGray,
    background = androidx.compose.ui.graphics.Color.White,
    onBackground = SoftBlack
)

@Composable
fun TechAssistantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
