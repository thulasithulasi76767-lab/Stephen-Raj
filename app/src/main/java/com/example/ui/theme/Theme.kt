package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Default is our bespoke luxury dark palette (Black, Gold, Cream) to preserve Stephen Enterprises brand identity
private val LuxuryDarkColorScheme = darkColorScheme(
  primary = ImperialGold,
  onPrimary = ObsidianBlack,
  primaryContainer = CharcoalSurface,
  onPrimaryContainer = ChampagneGold,
  secondary = SoftBeige,
  onSecondary = ObsidianBlack,
  secondaryContainer = CharcoalBorder,
  onSecondaryContainer = WarmCream,
  tertiary = ChampagneGold,
  onTertiary = ObsidianBlack,
  background = ObsidianBlack,
  onBackground = TextPrimaryDark,
  surface = DeepCharcoal,
  onSurface = TextPrimaryDark,
  surfaceVariant = CharcoalSurface,
  onSurfaceVariant = TextSecondaryDark,
  outline = CharcoalBorder,
  outlineVariant = GoldMuted,
  surfaceTint = ImperialGold
)

private val LuxuryLightColorScheme = lightColorScheme(
  primary = BronzeGold,
  onPrimary = Color.White,
  primaryContainer = SoftBeige,
  onPrimaryContainer = ObsidianBlack,
  secondary = DeepCharcoal,
  onSecondary = Color.White,
  secondaryContainer = SandBeige,
  onSecondaryContainer = ObsidianBlack,
  tertiary = ImperialGold,
  onTertiary = ObsidianBlack,
  background = WarmCream,
  onBackground = ObsidianBlack,
  surface = Color.White,
  onSurface = ObsidianBlack,
  surfaceVariant = SoftBeige,
  onSurfaceVariant = DeepCharcoal,
  outline = SandBeige,
  outlineVariant = GoldMuted,
  surfaceTint = BronzeGold
)

@Composable
fun StephenEnterprisesTheme(
  darkTheme: Boolean = true, // Default to ultra luxury dark experience
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) LuxuryDarkColorScheme else LuxuryLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
