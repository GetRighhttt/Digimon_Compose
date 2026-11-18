package com.getrighhttt.digimoncompose.presentation.mvilab

import kotlinx.coroutines.delay

/**
 * Simulates the same cancellable request for either framework.
 *
 * @author Stefan Bayne
 * @since 2026-11-18
 */
internal suspend fun loadSampleReport(simulateFailure: Boolean): ReportState {
    delay(900)
    return if (simulateFailure) {
        ReportState.Failed
    } else {
        ReportState.Loaded("Agumon was spotted near the forest trail. Stage: Child.")
    }
}
