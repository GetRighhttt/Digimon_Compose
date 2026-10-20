package com.getrighhttt.digimoncompose.app

import androidx.compose.runtime.Composable

/** Android dispatches system back; iOS uses the visible back action in this milestone. */
@Composable
internal expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)
