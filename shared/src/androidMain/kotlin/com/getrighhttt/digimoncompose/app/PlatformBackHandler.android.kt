package com.getrighhttt.digimoncompose.app

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

/**
 * Connects the shared selection shell to Android system back handling.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Composable
internal actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled, onBack)
}
