package com.getrighhttt.digimoncompose.presentation.mvilab

/**
 * Gives both demos the same screen states. Failure stays in state so retry remains visible.
 *
 * @author Stefan Bayne
 * @since 2026-11-18
 */
internal sealed interface ReportState {
    data object Ready : ReportState

    data object Loading : ReportState

    data class Loaded(
        val summary: String,
    ) : ReportState

    data object Failed : ReportState
}
