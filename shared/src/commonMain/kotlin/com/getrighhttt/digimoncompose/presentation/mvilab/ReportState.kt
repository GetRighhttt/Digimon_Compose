package com.getrighhttt.digimoncompose.presentation.mvilab

/**
 * Describes the visible phases of a sample field report request.
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
