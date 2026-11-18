package com.getrighhttt.digimoncompose.presentation.mvilab

import pro.respawn.flowmvi.api.MVIState

/**
 * Adapts the shared report model to FlowMVI’s state type.
 *
 * @author Stefan Bayne
 * @since 2026-11-18
 */
internal data class FlowReportState(
    val report: ReportState = ReportState.Ready,
) : MVIState
