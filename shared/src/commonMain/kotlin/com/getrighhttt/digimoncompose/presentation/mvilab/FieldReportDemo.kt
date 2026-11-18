package com.getrighhttt.digimoncompose.presentation.mvilab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import pro.respawn.flowmvi.dsl.collect

/**
 * Connects each framework to the same report screen for its route lifetime.
 *
 * @author Stefan Bayne
 * @since 2026-11-18
 */
@Composable
internal fun FieldReportDemo(
    demo: MviDemo,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    when (demo) {
        MviDemo.FlowMvi -> {
            val store = remember { flowReportStore() }
            DisposableEffect(store, scope) {
                val lifecycle = store.start(scope)
                onDispose { lifecycle.close() }
            }
            var state by remember { mutableStateOf<ReportState>(ReportState.Ready) }
            val lifecycle = LocalLifecycleOwner.current.lifecycle
            LaunchedEffect(store, lifecycle) {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    store.collect { states.collect { state = it.report } }
                }
            }
            FieldReportScreen(
                framework = demo.label,
                state = state,
                onBack = onBack,
                onLoad = { store.intent(ReportIntent.Load) },
                onFail = { store.intent(ReportIntent.SimulateFailure) },
                onRetry = { store.intent(ReportIntent.Retry) },
            )
        }

        MviDemo.Orbit -> {
            val host = remember(scope) { OrbitReportHost(scope) }
            val state by host.container.externalStateFlow.collectAsStateWithLifecycle()
            FieldReportScreen(
                framework = demo.label,
                state = state,
                onBack = onBack,
                onLoad = { host.load() },
                onFail = { host.load(simulateFailure = true) },
                onRetry = { host.load() },
            )
        }
    }
}
