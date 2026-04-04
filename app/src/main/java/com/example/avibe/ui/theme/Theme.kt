package com.example.avibe.ui.theme

import android.app.Activity
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


val DarkBlueBackground = Color(0xFF0A0E17)        // Глубокий тёмно-синий фон
val CardBackground = Color(0xFF151C29)            // Фон карточек
val CardBorder = Color(0xFF3B82F6)                // Неоновый синий контур
val CardBorderGlow = Color(0xFF60A5FA)            // Свечение контура
val TextPrimary = Color(0xFFF1F5F9)               // Белый текст
val TextSecondary = Color(0xFF94A3B8)             // Серый текст
val AccentPurple = Color(0xFF8B5CF6)              // Фиолетовый акцент
val AccentCyan = Color(0xFF22D3EE)

val DarkColorScheme = darkColorScheme(
    primary = AccentPurple,
    secondary = AccentCyan,
    background = DarkBlueBackground,
    surface = CardBackground,
    onPrimary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    primaryContainer = CardBackground.copy(alpha = 0.8f),
    onPrimaryContainer = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun AVibeTheme(
    darkTheme:Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor:Boolean = true,
    content:@Composable () -> Unit
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