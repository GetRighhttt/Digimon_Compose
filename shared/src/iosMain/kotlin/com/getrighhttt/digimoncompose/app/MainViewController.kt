package com.getrighhttt.digimoncompose.app

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * Hosts shared Compose on iOS and closes both feature stores and their HTTP session
 * when the composition is disposed.
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
