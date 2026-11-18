package com.getrighhttt.digimoncompose.app

import androidx.compose.runtime.Composable

/**
 * On iOS, use the back button drawn by the shared screen.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Composable
internal actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) = Unit
