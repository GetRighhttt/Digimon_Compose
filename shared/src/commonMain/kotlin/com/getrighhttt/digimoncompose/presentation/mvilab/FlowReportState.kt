package com.getrighhttt.digimoncompose.presentation.mvilab

import pro.respawn.flowmvi.api.MVIState

/**
 * Implements FlowMVI's state interface so both demos can use the same report model.
 *
 * @author Stefan Bayne
 * @since 2026-11-18
 */
internal data class FlowReportState(
    val report: ReportState = ReportState.Ready,
) : MVIState
