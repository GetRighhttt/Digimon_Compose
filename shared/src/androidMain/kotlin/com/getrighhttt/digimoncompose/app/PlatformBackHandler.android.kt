package com.getrighhttt.digimoncompose.app

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

/**
 * Lets the shared app close its current screen when the user presses Android back.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Composable
internal actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    BackHandler(enabled, onBack)
}
