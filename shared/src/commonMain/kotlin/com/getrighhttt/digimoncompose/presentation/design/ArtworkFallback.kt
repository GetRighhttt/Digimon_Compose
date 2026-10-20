package com.getrighhttt.digimoncompose.presentation.design

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

/**
 * Shows a name initial while artwork loads or is unavailable on either screen.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Composable
internal fun ArtworkFallback(name: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        GuideText(name.take(1), size = 40, color = Color(0xFF006B5B), weight = FontWeight.Bold)
    }
}
