package com.getrighhttt.digimoncompose.app

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * Hosts the shared Compose UI. Disposing it closes the stores and HTTP client.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
fun createMainViewController(): UIViewController =
    ComposeUIViewController {
        val scope = rememberCoroutineScope()
        val session = remember { BrowseSession(scope) }
        DisposableEffect(session) { onDispose { session.close() } }
        DigimonApp(session.store, session.profileStore)
    }
