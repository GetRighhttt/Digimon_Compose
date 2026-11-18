package com.getrighhttt.digimoncompose.presentation.mvilab

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.SettingsBuilder
import org.orbitmvi.orbit.orbitContainer

/**
 * Exposes a load function that runs an Orbit intent. The intent does the work and calls
 * reduce to publish loading and the result.
 *
 * @author Stefan Bayne
 * @since 2026-11-18
 */
internal class OrbitReportHost(
    scope: CoroutineScope,
    settings: SettingsBuilder.() -> Unit = {},
) : OrbitContainerHost<ReportState, ReportState, Nothing> {
    private val request = Mutex()

    override val container = scope.orbitContainer<ReportState, Nothing>(ReportState.Ready, settings)

    fun load(simulateFailure: Boolean = false) =
        intent {
            // Orbit intents can overlap while a load waits. Skip a second request.
            if (!request.tryLock()) return@intent

            try {
                reduce { ReportState.Loading }
                val result = loadSampleReport(simulateFailure)
                reduce { result }
            } finally {
                request.unlock()
            }
        }
}
