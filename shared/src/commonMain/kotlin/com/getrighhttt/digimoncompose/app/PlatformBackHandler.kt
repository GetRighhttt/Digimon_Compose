package com.getrighhttt.digimoncompose.app

import androidx.compose.runtime.Composable

/**
 * Defines platform back handling for the shared selection shell. iOS uses its visible back action.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Composable
internal expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)
