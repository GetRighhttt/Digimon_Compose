package com.getrighhttt.digimoncompose.app

import androidx.compose.runtime.Composable

/**
 * Lets the app handle Android system back. Both platforms have a visible back button.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Composable
internal expect fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
)
