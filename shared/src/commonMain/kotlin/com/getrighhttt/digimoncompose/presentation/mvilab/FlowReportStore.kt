package com.getrighhttt.digimoncompose.presentation.mvilab

import kotlinx.coroutines.sync.Mutex
import pro.respawn.flowmvi.api.ActionShareBehavior
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.reduce

/**
 * Routes typed UI intents through FlowMVI's reduce plugin. The store publishes loading
 * and the result as separate state updates.
 *
 * @author Stefan Bayne
 * @since 2026-11-18
 */
internal fun flowReportStore() =
    store<FlowReportState, ReportIntent, Nothing>(FlowReportState()) {
        val request = Mutex()

        configure {
            actionShareBehavior = ActionShareBehavior.Disabled
            // Let a second intent reach the lock now instead of queuing it behind the load.
            parallelIntents = true
        }

        reduce { intent ->
            if (!request.tryLock()) return@reduce

            try {
                updateState { copy(report = ReportState.Loading) }
                val result =
                    loadSampleReport(
                        simulateFailure =
                            intent == ReportIntent.SimulateFailure,
                    )
                updateState { copy(report = result) }
            } finally {
                request.unlock()
            }
        }
    }
