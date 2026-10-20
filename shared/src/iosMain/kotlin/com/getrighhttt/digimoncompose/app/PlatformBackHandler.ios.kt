package com.getrighhttt.digimoncompose.app

import androidx.compose.runtime.Composable

/**
 * Leaves system back unregistered on iOS, where the profile screen provides a visible
 * back action.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Composable
internal actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) = Unit
