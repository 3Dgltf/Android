package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppThemePreset(val displayName: String, val accentColor: Color) {
    SOPHISTICATED_DARK("Sophisticated Dark", SophisticatedAccent),
    OBSIDIAN_AMBER("Obsidian Amber", ObsidianAccent),
    MIDNIGHT_CYAN("Midnight Cyan", CyberAccent),
    EMERALD_TITANIUM("Emerald Slate", EmeraldAccent),
    ROYAL_VIOLET("Royal Violet", VioletAccent),
    MINIMAL_LIGHT("Minimal Light", LightAccent)
}

@Immutable
data class CalculatorCustomColors(
    val background: Color,
    val surface: Color,
    val cardBackground: Color,
    val keyDigit: Color,
    val keyOperator: Color,
    val keyFunction: Color,
    val keyEquals: Color,
    val keyEqualsGlow: Color,
    val onKeyEquals: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textOperator: Color,
    val textFunction: Color,
    val accentCyan: Color,
    val handleColor: Color,
    val borderGlow: Color,
    val isDark: Boolean = true
)

val LocalCalculatorColors = staticCompositionLocalOf {
    getCustomColorsForPreset(AppThemePreset.SOPHISTICATED_DARK)
}

fun getCustomColorsForPreset(preset: AppThemePreset): CalculatorCustomColors {
    return when (preset) {
        AppThemePreset.SOPHISTICATED_DARK -> CalculatorCustomColors(
            background = SophisticatedBg,
            surface = SophisticatedSurface,
            cardBackground = SophisticatedCard,
            keyDigit = SophisticatedKeyDigit,
            keyOperator = SophisticatedKeyOp,
            keyFunction = SophisticatedKeyFunc,
            keyEquals = SophisticatedAccent,
            keyEqualsGlow = SophisticatedAccentGlow,
            onKeyEquals = SophisticatedOnAccent,
            textPrimary = SophisticatedTextPrimary,
            textSecondary = SophisticatedTextSecondary,
            textTertiary = SophisticatedTextTertiary,
            textOperator = SophisticatedTextOp,
            textFunction = SophisticatedTextOp,
            accentCyan = SophisticatedCyanDot,
            handleColor = SophisticatedHandle,
            borderGlow = Color(0x33D1E4FF),
            isDark = true
        )
        AppThemePreset.OBSIDIAN_AMBER -> CalculatorCustomColors(
            background = ObsidianBg,
            surface = ObsidianSurface,
            cardBackground = ObsidianCard,
            keyDigit = ObsidianKeyDigit,
            keyOperator = ObsidianKeyOp,
            keyFunction = ObsidianKeyFunc,
            keyEquals = ObsidianAccent,
            keyEqualsGlow = ObsidianAccentGlow,
            onKeyEquals = Color.Black,
            textPrimary = ObsidianTextPrimary,
            textSecondary = ObsidianTextSecondary,
            textTertiary = ObsidianTextTertiary,
            textOperator = ObsidianAccent,
            textFunction = ObsidianTextSecondary,
            accentCyan = Color(0xFFFFB703),
            handleColor = Color(0xFF2C3244),
            borderGlow = Color(0x33FF9F1C),
            isDark = true
        )
        AppThemePreset.MIDNIGHT_CYAN -> CalculatorCustomColors(
            background = CyberBg,
            surface = CyberSurface,
            cardBackground = CyberCard,
            keyDigit = CyberKeyDigit,
            keyOperator = CyberKeyOp,
            keyFunction = CyberKeyFunc,
            keyEquals = CyberAccent,
            keyEqualsGlow = CyberAccentGlow,
            onKeyEquals = Color.Black,
            textPrimary = Color(0xFFF0FDF4),
            textSecondary = Color(0xFF94A3B8),
            textTertiary = Color(0xFF64748B),
            textOperator = CyberAccent,
            textFunction = Color(0xFF94A3B8),
            accentCyan = CyberAccent,
            handleColor = Color(0xFF223257),
            borderGlow = Color(0x3306B6D4),
            isDark = true
        )
        AppThemePreset.EMERALD_TITANIUM -> CalculatorCustomColors(
            background = EmeraldBg,
            surface = EmeraldSurface,
            cardBackground = EmeraldCard,
            keyDigit = EmeraldKeyDigit,
            keyOperator = EmeraldKeyOp,
            keyFunction = EmeraldKeyFunc,
            keyEquals = EmeraldAccent,
            keyEqualsGlow = EmeraldAccentGlow,
            onKeyEquals = Color.Black,
            textPrimary = Color(0xFFECFDF5),
            textSecondary = Color(0xFF94A3B8),
            textTertiary = Color(0xFF64748B),
            textOperator = EmeraldAccent,
            textFunction = Color(0xFF94A3B8),
            accentCyan = Color(0xFF34D399),
            handleColor = Color(0xFF214234),
            borderGlow = Color(0x3310B981),
            isDark = true
        )
        AppThemePreset.ROYAL_VIOLET -> CalculatorCustomColors(
            background = VioletBg,
            surface = VioletSurface,
            cardBackground = VioletCard,
            keyDigit = VioletKeyDigit,
            keyOperator = VioletKeyOp,
            keyFunction = VioletKeyFunc,
            keyEquals = VioletAccent,
            keyEqualsGlow = VioletAccentGlow,
            onKeyEquals = Color.White,
            textPrimary = Color(0xFFFAF5FF),
            textSecondary = Color(0xFF94A3B8),
            textTertiary = Color(0xFF64748B),
            textOperator = VioletAccentGlow,
            textFunction = Color(0xFF94A3B8),
            accentCyan = Color(0xFFA78BFA),
            handleColor = Color(0xFF30255E),
            borderGlow = Color(0x338B5CF6),
            isDark = true
        )
        AppThemePreset.MINIMAL_LIGHT -> CalculatorCustomColors(
            background = LightBg,
            surface = LightSurface,
            cardBackground = LightCard,
            keyDigit = LightKeyDigit,
            keyOperator = LightKeyOp,
            keyFunction = LightKeyFunc,
            keyEquals = LightAccent,
            keyEqualsGlow = LightAccentGlow,
            onKeyEquals = Color.White,
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            textTertiary = Color(0xFF94A3B8),
            textOperator = LightAccent,
            textFunction = LightTextSecondary,
            accentCyan = Color(0xFF3B82F6),
            handleColor = Color(0xFFCBD5E1),
            borderGlow = Color(0x222563EB),
            isDark = false
        )
    }
}

@Composable
fun CalculatorTheme(
    preset: AppThemePreset = AppThemePreset.SOPHISTICATED_DARK,
    content: @Composable () -> Unit
) {
    val customColors = getCustomColorsForPreset(preset)

    val colorScheme = if (customColors.isDark) {
        darkColorScheme(
            primary = customColors.keyEquals,
            onPrimary = customColors.onKeyEquals,
            surface = customColors.surface,
            onSurface = customColors.textPrimary,
            background = customColors.background,
            onBackground = customColors.textPrimary
        )
    } else {
        lightColorScheme(
            primary = customColors.keyEquals,
            onPrimary = customColors.onKeyEquals,
            surface = customColors.surface,
            onSurface = customColors.textPrimary,
            background = customColors.background,
            onBackground = customColors.textPrimary
        )
    }

    CompositionLocalProvider(LocalCalculatorColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
