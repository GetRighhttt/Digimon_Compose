package com.getrighhttt.digimoncompose.presentation.mvilab

import pro.respawn.flowmvi.api.MVIIntent

/**
 * Names the requests the UI can send to the FlowMVI store.
 *
 * @author Stefan Bayne
 * @since 2026-11-18
 */
internal enum class ReportIntent : MVIIntent {
    Load,
    SimulateFailure,
    Retry,
}
