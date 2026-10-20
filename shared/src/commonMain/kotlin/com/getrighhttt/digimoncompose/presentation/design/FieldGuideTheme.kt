package com.getrighhttt.digimoncompose.presentation.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Supplies the shared palette for system light and dark appearance.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal val LocalFieldGuideColors =
    staticCompositionLocalOf {
        FieldGuideColors(
            background = Color(0xFFF5F7F4),
            surface = Color.White,
            text = Color(0xFF182C29),
            secondary = Color(0xFF596C67),
            accent = Color(0xFF006B5B),
            onAccent = Color.White,
            tint = Color(0xFFDDEEE6),
            border = Color(0xFFE0E7E1),
            error = Color(0xFFA12D39),
            cover = Color(0xFF123F36),
            onCover = Color(0xFFF0F8F2),
        )
    }

@Composable
internal fun FieldGuideTheme(content: @Composable () -> Unit) {
    val colors =
        if (isSystemInDarkTheme()) {
            FieldGuideColors(
                background = Color(0xFF111D1B),
                surface = Color(0xFF1C2B28),
                text = Color(0xFFEDF5EF),
                secondary = Color(0xFFB0C5BC),
                accent = Color(0xFF85DAC2),
                onAccent = Color(0xFF002D24),
                tint = Color(0xFF29483D),
                border = Color(0xFF354A43),
                error = Color(0xFFFFB4B9),
                cover = Color(0xFF204C40),
                onCover = Color(0xFFF0F8F2),
            )
        } else {
            LocalFieldGuideColors.current
        }
    CompositionLocalProvider(LocalFieldGuideColors provides colors, content = content)
}
