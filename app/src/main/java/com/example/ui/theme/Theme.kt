package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = BluePrimary,
  onPrimary = White,
  primaryContainer = BlueLight,
  onPrimaryContainer = BlueDark,
  secondary = GreenIcon,
  onSecondary = White,
  background = White,
  onBackground = Gray900,
  surface = White,
  onSurface = Gray900,
  surfaceVariant = Gray50,
  onSurfaceVariant = Gray600,
  outline = Gray200
)

private val DarkColorScheme = darkColorScheme(
  primary = BluePrimary,
  onPrimary = White,
  background = Gray900,
  onBackground = White,
  surface = Color(0xFF1E2430),
  onSurface = White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Force crisp clean light theme as requested
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
