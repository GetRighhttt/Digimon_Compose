package com.getrighhttt.digimoncompose.app

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/** The controller's composition owns the store; closing it cancels Browse work. */
fun MainViewController(): UIViewController = ComposeUIViewController {
    val scope = rememberCoroutineScope()
    val session = remember { BrowseSession(scope) }
    DisposableEffect(session) { onDispose { session.close() } }
    DigimonApp(session.store, session.profileStore)
}
