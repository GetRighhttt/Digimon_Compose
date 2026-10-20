package com.getrighhttt.digimoncompose.presentation.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

internal val LocalFieldGuideColors = staticCompositionLocalOf {
    FieldGuideColors(Color(0xFFF5F7F4), Color.White, Color(0xFF182C29), Color(0xFF596C67),
        Color(0xFF006B5B), Color.White, Color(0xFFDDEEE6), Color(0xFFE0E7E1), Color(0xFFA12D39))
}

/** Both platforms follow the system appearance with the same semantic palette. */
@Composable
internal fun FieldGuideTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) {
        FieldGuideColors(Color(0xFF111D1B), Color(0xFF1C2B28), Color(0xFFEDF5EF), Color(0xFFB0C5BC),
            Color(0xFF85DAC2), Color(0xFF002D24), Color(0xFF29483D), Color(0xFF354A43), Color(0xFFFFB4B9))
    } else LocalFieldGuideColors.current
    CompositionLocalProvider(LocalFieldGuideColors provides colors, content = content)
}
